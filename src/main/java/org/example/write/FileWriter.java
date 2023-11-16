/**
 * The FileWriter interface defines a contract for classes responsible for writing content from a specified URI
 * to a local file at the given path. Implementing classes should handle the process of downloading and saving
 * content to the local filesystem.
 */
package org.example.write;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;

public interface FileWriter {

    /**
     * Writes content from the specified URI to the local file at the given path.
     *
     * @param uri  The URI of the content to be written.
     * @param path The local path where the content should be saved.
     * @throws IOException If an I/O error occurs during the writing process.
     */
    void write(URI uri, Path path) throws IOException;
}
