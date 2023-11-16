package org.example.crawl;

import org.example.read.PageReader;
import org.example.utils.Resources;
import org.example.write.FileWriter;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import static org.mockito.Mockito.*;

/**
 * Unit test for the Crawler class.
 */
class CrawlerTest {

    // URL of the index page for testing
    private final URL INDEX_PAGE = new URL("https://books.toscrape.com/index.html");

    // Mock objects for PageReader and FileWriter
    private PageReader pageReaderMock;
    private FileWriter fileWriterMock;

    CrawlerTest() throws MalformedURLException {
    }

    /**
     * Set up the test environment.
     * Mocks the PageReader and FileWriter objects, and provides a mock HTML document for the index page.
     *
     * @throws IOException        If an I/O error occurs during the setup.
     * @throws URISyntaxException If there is an error in the URI syntax.
     */
    @BeforeEach
    void setUp() throws IOException, URISyntaxException {
        // Create mock objects for PageReader and FileWriter
        pageReaderMock = mock(PageReader.class);
        fileWriterMock = mock(FileWriter.class);

        // Set up the base URI for the mock HTML document
        String baseUri = "https://books.toscrape.com/";

        // When reading the index page URI, return a mock HTML document
        when(pageReaderMock.read(INDEX_PAGE.toURI())).thenReturn(Jsoup.parse(Resources.getIndexPage(), baseUri));
    }

    /**
     * Test the start method of the Crawler class.
     * Validates that all endpoints are extracted as expected, and the crawler finishes successfully.
     *
     * @throws IOException If an I/O error occurs during the test.
     */
    @Test
    void start() throws IOException {
        // Create a Crawler instance with the mock objects and test parameters
        Crawler crawler = new Crawler(pageReaderMock, fileWriterMock, 20, "test-data", INDEX_PAGE);

        // Start the crawler with a progress consumer that does nothing
        crawler.start(progress -> {});

        // Verify that the write method of FileWriter is called 29 times
        verify(fileWriterMock, times(29)).write(any(), any());
    }
}
