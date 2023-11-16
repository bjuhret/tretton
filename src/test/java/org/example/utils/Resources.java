package org.example.utils;

import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class Resources {

    public static String getIndexPage() {

        ClassLoader classLoader = Resources.class.getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("index.html");

        assertNotNull(inputStream);

        return readInputStreamToString(inputStream);
    }

    private static String readInputStreamToString(InputStream inputStream) {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            return IOUtils.toString(reader);
        } catch (Exception e) {
            throw new RuntimeException("Failed to read InputStream to String", e);
        }
    }
}
