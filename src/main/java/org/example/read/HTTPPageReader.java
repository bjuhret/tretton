/**
 * HTTPPageReader is an implementation of the PageReader interface that reads a web page
 * from the specified URI using Jsoup library.
 */
package org.example.read;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import java.io.IOException;
import java.net.URI;

public class HTTPPageReader implements PageReader {

    /**
     * Reads a web page from the given URI using Jsoup library.
     *
     * @param uri The URI of the web page to read.
     * @return A Jsoup Document representing the parsed web page.
     * @throws IOException If an I/O error occurs during the page retrieval.
     */
    @Override
    public Document read(URI uri) throws IOException {
        return Jsoup.connect(uri.toURL().toString()).get();
    }
}
