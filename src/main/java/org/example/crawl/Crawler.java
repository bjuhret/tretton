package org.example.crawl;

import org.apache.commons.io.FileUtils;
import org.example.misc.Pair;
import org.example.read.PageReader;
import org.example.write.FileWriter;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The Crawler class is responsible for recursively crawling a website, downloading and saving its pages and resources.
 * It utilizes multithreading to improve performance during the crawling process and pre-order traversal for memory
 * efficiency.
 */
public class Crawler {

    // Constants
    private static final String WORKING_DIR = Paths.get("").toAbsolutePath().toString();
    public static final Set<Pair<String, String>> RESOURCE_MAPPING = Set.of(
            new Pair("img[src]", "src"), new Pair("link[href]", "href"), new Pair("script[src]", "src"));
    public static final Set<Pair<String, String>> LINK_MAPPING = Set.of(new Pair("a[href]", "href"));

    // Member variables
    private final PageReader reader;
    private final FileWriter writer;
    private final AtomicInteger persisted = new AtomicInteger();
    private final AtomicInteger jobs = new AtomicInteger();
    private final URL url;
    private final URL baseURL;
    private final String outputDirectory;
    private final ExtendedExecutor executor;

    /**
     * Constructs a new Crawler.
     *
     * @param reader          The page reader to retrieve web pages.
     * @param writer          The file writer to save pages and resources locally.
     * @param threads         The number of threads for parallel processing.
     * @param outputDirectory The local directory to save downloaded content.
     * @param url             The starting URL of the website to crawl.
     */
    public Crawler(PageReader reader, FileWriter writer, int threads, String outputDirectory, URL url) {

        // Validate the number of threads
        if (threads < 1) {
            throw new IllegalArgumentException("The number of threads must be greater than zero");
        }

        // Ensure non-null parameters
        throwIfNull(reader, "reader");
        throwIfNull(writer, "writer");
        throwIfNull(outputDirectory, "outputDirectory");
        throwIfNull(url, "url");

        // Initialize member variables
        this.reader = reader;
        this.writer = writer;
        this.executor = new ExtendedExecutor(threads);
        this.url = url;

        try {
            // Derive the baseURL from the provided URL
            this.baseURL = new URL(url, ".");
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        this.outputDirectory = outputDirectory;
    }

    /**
     * Throws a {@link IllegalArgumentException} if the specified parameter is null.
     *
     * @param param     The parameter to check for null.
     * @param paramName The name of the parameter (for error message clarity).
     * @throws IllegalArgumentException If the specified parameter is null.
     */
    private void throwIfNull(Object param, String paramName) {
        if (param == null) throw new IllegalArgumentException("Parameter " + paramName + " is null");
    }

    /**
     * Starts the crawling process and monitors progress.
     *
     * @param progressConsumer A consumer to receive progress updates.
     */
    public void start(Consumer<Progress> progressConsumer) {
        try {
            // Delete the data directory if it exists
            deleteDataDirectoryIfExists();

            long start = System.currentTimeMillis();

            // Submit the initial crawling job
            submitJob(JobType.Page, this.url.toURI());

            // Continue monitoring progress until the executor is terminated
            while (!executor.isTerminated()) {
                if (progressConsumer != null) {
                    // Notify the progress consumer
                    progressConsumer.accept(new Progress(persisted.get(), jobs.get(),
                            (System.currentTimeMillis() - start) / 1000, executor.exception));
                }

                // If the execution is done, shut down the executor if not already shut down
                if (isExecutionDone()) {
                    if (!executor.isShutdown()) {
                        executor.shutdown();
                    }
                } else {
                    try {
                        // Sleep for 1 second before checking progress again
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        // Handle interruption or reassert the interrupted status
                        Thread.currentThread().interrupt();
                    }
                }
            }
        } catch (Exception e) {
            // Propagate any exceptions that occur during the crawling process
            throw new RuntimeException(e);
        }
    }

    /**
     * Deletes the data directory if it exists.
     *
     * @throws IOException If an I/O error occurs while deleting the directory.
     */
    private void deleteDataDirectoryIfExists() throws IOException {
        // Construct the path to the data directory
        Path output = Paths.get(WORKING_DIR, outputDirectory);

        // Delete the directory and its contents if it exists
        if(output.toFile().exists() && output.toFile().isDirectory()) {
            FileUtils.deleteDirectory(output.toFile());
        }
    }

    /**
     * Checks if the crawling execution is complete by inspecting the number of active jobs.
     *
     * @return {@code true} if there are no active jobs, indicating that the crawling is complete;
     * {@code false} otherwise.
     */
    private boolean isExecutionDone() {
        return jobs.get() == 0;
    }

    /**
     * Converts a URI to a local file path based on the working directory and output directory.
     *
     * @param uri The URI to convert.
     * @return The local file path.
     */
    private Path asPath(URI uri) {
        return Paths.get(WORKING_DIR, outputDirectory, uri.getPath());
    }

    /**
     * Performs the crawling work for a given type and URI.
     */
    private void doWork(JobType jobType, URI uri) {
        try {
            // Extract URI and construct local file path
            Path path = asPath(uri);

            // Check if the file doesn't exist to avoid duplicate processing and infinite recursion
            if (!path.toFile().exists()) {
                // Create directory structure and file
                Files.createDirectories(path.getParent());
                Files.createFile(path);
                persisted.incrementAndGet();

                // Determine the type of job (File or Page)
                switch (jobType) {

                    case File:
                        // For File type, use the writer to save the resource locally
                        this.writer.write(uri, path);
                        break;
                    case Page:
                        // For Page type, read the document and extract resources and links
                        Document document = this.reader.read(uri);

                        // Extract resources (images, links, scripts) in the domain
                        Set<String> resources = extractResourceUrls(document, RESOURCE_MAPPING);

                        // Submit jobs for resources
                        for (String resource : resources) {
                            try {
                                submitJob(JobType.File, new URI(resource));
                            } catch (URISyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        // Extract links in the domain and submit jobs for pages
                        Set<String> links = extractResourceUrls(document, LINK_MAPPING);
                        for (String link : links) {
                            try {
                                submitJob(JobType.Page, new URI(link));
                            } catch (URISyntaxException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        // Save the page locally
                        this.writer.write(uri, path);
                        break;
                    default:
                        // Throw an exception for unexpected job types
                        throw new IllegalStateException("Unexpected type " + jobType.name());
                }
            }
        } catch (FileAlreadyExistsException e) {
            // Ignore if another thread has created the file already, this is expected
        } catch (IOException e) {
            // Propagate any IOException that occurs during the crawling process
            throw new RuntimeException(e);
        }
    }

    /**
     * Extracts resource URLs from a document based on specified CSS queries and attribute keys.
     *
     * @param document            The document to extract resources from.
     * @param queryAttributePairs A set of pairs representing CSS queries and attribute keys
     *                            for extracting resource URLs.
     * @return A set of resource URLs extracted from the document.
     */
    private Set<String> extractResourceUrls(Document document, Set<Pair<String, String>> queryAttributePairs) {
        // Map each pair of CSS query and attribute key to a set of URLs and combine them into a single set
        return queryAttributePairs.stream()
                .map(pair -> extractUrlsInDomain(document, pair.getFirst(), pair.getSecond()))
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    /**
     * Extracts URLs from a document based on a CSS query and attribute key, filtering by the base URL.
     *
     * @param document     The document to extract URLs from.
     * @param cssQuery     The CSS query to select elements.
     * @param attributeKey The attribute key containing the URL.
     * @return A set of URLs within the domain.
     */
    private Set<String> extractUrlsInDomain(Document document, String cssQuery, String attributeKey) {
        return document.select(cssQuery)
                .stream()
                .map(link -> link.absUrl(attributeKey))
                .filter(url -> url.toLowerCase().startsWith(baseURL.toString()))
                .collect(Collectors.toSet());
    }

    /**
     * Submits a crawling job to the executor.
     */
    private void submitJob(JobType jobType, URI uri) {
        // Increment the number of active jobs
        jobs.incrementAndGet();

        // Submit the job to the executor
        executor.submit(() -> {
            try {
                // Perform the crawling work
                doWork(jobType, uri);
            } catch (Exception e) {
                // Propagate any exceptions that occur during crawling
                throw new RuntimeException(e);
            } finally {
                // Decrement the number of active jobs
                jobs.decrementAndGet();
            }
        });
    }
}
