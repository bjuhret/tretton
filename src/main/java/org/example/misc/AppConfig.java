package org.example.misc;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

/**
 * Utility class for accessing application configuration properties.
 */
public class AppConfig {
    // Load the default configuration
    private static final Config config = ConfigFactory.load();

    /**
     * Gets the output directory from the configuration.
     *
     * @return The output directory.
     */
    public static String getOutputDirectory() {
        return config.getString("outputDirectory");
    }

    /**
     * Gets the source URL from the configuration.
     *
     * @return The source URL.
     */
    public static String getSourceURL() {
        return config.getString("sourceUrl");
    }
}
