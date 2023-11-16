/**
 * The PageReader interface defines a contract for classes that read web pages from a URI
 * and return a Jsoup Document representing the parsed content.
 */
package org.example.read;

import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.URI;

public interface PageReader {

    /**
     * Reads a web page from the specified URI and returns a Jsoup Document.
     *
     * @param uri The URI of the web page to read.
     * @return A Jsoup Document representing the parsed web page.
     * @throws IOException If an I/O error occurs during the page retrieval.
     */
    Document read(URI uri) throws IOException;
}
