/**
 * The HTTPFileWriter class implements the FileWriter interface and is responsible for writing content from a specified
 * URI to a local file using HTTP connections. It utilizes InputStream, ReadableByteChannel, FileOutputStream, and
 * FileChannel for efficient data transfer.
 */
package org.example.write;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;

public class BlockingFileWriter implements FileWriter {

    /**
     * Writes content from the specified URI to the local file at the given path using HTTP connections.
     *
     * @param uri  The URI of the content to be written.
     * @param path The local path where the content should be saved.
     * @throws IOException If an I/O error occurs during the writing process.
     */
    @Override
    public void write(URI uri, Path path) throws IOException {
        try (InputStream inputStream = uri.toURL().openStream();
             ReadableByteChannel readableByteChannel = Channels.newChannel(inputStream);
             FileOutputStream fileOutputStream = new FileOutputStream(path.toFile());
             FileChannel fileChannel = fileOutputStream.getChannel()) {
            fileChannel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        }
    }
}
