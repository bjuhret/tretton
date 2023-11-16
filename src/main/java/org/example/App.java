/**
 * The App class serves as the entry point for the application and provides a command-line interface for configuring
 * and running the web crawling process.
 */
package org.example;

import org.apache.commons.cli.*;
import org.example.crawl.Crawler;
import org.example.misc.AppConfig;
import org.example.read.HTTPPageReader;
import org.example.write.NoneBlockingFileWriter;
import org.example.write.FileWriter;
import org.example.write.BlockingFileWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class App {

    // Constants
    private static final URL SOURCE_URL;
    private static final String OUTPUT_DIRECTORY = AppConfig.getOutputDirectory();

    static {
        try {
            SOURCE_URL = new URL(AppConfig.getSourceURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * The main method that parses command-line arguments, configures and initiates the web crawling process.
     *
     * @param args Command-line arguments.
     */
    public static void main(String[] args) {

        // Configure command-line options
        Options options = new Options();
        options.addOption(Option.builder("t").hasArg().longOpt("threads").argName("threads").required(false)
                .desc("Overrides the number of threads used. Defaults to twice the number of cores available.").build());
        options.addOption(Option.builder("a").required(false)
                .desc("Downloads files asynchronously if set. Use for slow network connections. " +
                        "Can negatively affect performance if the connection times are fast.").build());

        CommandLineParser parser = new DefaultParser();
        HelpFormatter helper = new HelpFormatter();

        //  best guess...
        int threads = Runtime.getRuntime().availableProcessors() * 2;
        FileWriter writer = new BlockingFileWriter();

        try {
            CommandLine cmd = parser.parse(options, args);

            // Set writer to asynchronous if the -a option is present
            if (cmd.hasOption("a")) {
                writer = new NoneBlockingFileWriter();
            }

            // Override the number of threads if the -t option is present
            if (cmd.hasOption("t")) {
                try {
                    threads = Integer.parseInt(cmd.getOptionValue("t").trim());
                } catch (NumberFormatException e) {
                    throw new ParseException("The value '" + cmd.getOptionValue("t").trim()
                            + "' for the number of threads cannot be parsed to a number");
                }
                System.out.println("Config t set to " + cmd.getOptionValue("t").trim());
            }

//            System.out.println("Just exit for now " + App.OUTPUT_DIRECTORY);
//            System.exit(0);

            // Initialize and start the web crawler
            Crawler crawler =
                    new Crawler(new HTTPPageReader(), writer, threads, OUTPUT_DIRECTORY, SOURCE_URL);
            System.out.println("Starting file download from " + SOURCE_URL + " using " + threads + " thread(s) and a " +
                    (writer instanceof NoneBlockingFileWriter ? "asynchronous writer " : "synchronous writer "));
            crawler.start(progress -> {
                if (progress.getException() != null) {
                    throw progress.getException();
                }
                System.out.print("Completed " + progress.getPersisted() + " | Scheduled " +
                        progress.getScheduled() + " | Elapsed " + progress.getElapsedTimeInSeconds() + "(s)  "
                        + "                            \r");
            });
            System.out.println("\nDownload complete");

        } catch (ParseException e) {
            System.out.println(e.getMessage());
            helper.printHelp("Usage:", options);
            System.exit(0);
        } catch (Exception e) {
            System.out.println("There was an exception");
            System.out.println(e.getCause().getMessage());
            System.out.println("Execution will be terminated. Try again with fewer threads and synchronous download");
            System.exit(0);
        }
    }
}
