/**
 * The AsyncHTTPFileWriter class implements the FileWriter interface and is responsible for asynchronously
 * downloading a file from a given URI and writing it to the specified local path.
 */
package org.example.write;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;

public class NoneBlockingFileWriter implements FileWriter {

    // HttpClient instance for handling HTTP requests
    private final HttpClient client = HttpClient.newHttpClient();

    /**
     * Downloads a file from the specified URI asynchronously and writes it to the specified local path.
     *
     * @param uri  The URI of the file to download.
     * @param path The local path to save the downloaded file.
     * @throws IOException If an I/O error occurs during the download or file writing process.
     */
    @Override
    public void write(URI uri, Path path) throws IOException {
        // Build an HTTP GET request for the specified URI
        HttpRequest request = HttpRequest.newBuilder().uri(uri).GET().build();

        // Asynchronously send the HTTP request and handle the response
        try (InputStream is = client.sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
                .thenApply(HttpResponse::body).join()) {
            // Write the downloaded file content to the local file
            try (FileOutputStream out = new FileOutputStream(path.toFile())) {
                is.transferTo(out);
            }
        }
    }
}
