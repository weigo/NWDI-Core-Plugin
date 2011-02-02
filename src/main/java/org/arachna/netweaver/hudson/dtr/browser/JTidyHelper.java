/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.w3c.dom.Document;
import org.w3c.tidy.Tidy;

/**
 * Helper class for using JTidy.
 * 
 * @author Dirk Weigenand
 */
final class JTidyHelper {
    /**
     * static helper class: should not be instantiated.
     */
    private JTidyHelper() {
    }

    /**
     * Parse HTML from {@link java.io.InputStream} and return a
     * {@link org.w3c.dom.Document}.
     * 
     * @param input
     *            the InputStream containing the HTML that should be parsed into
     *            a Document
     * @return Document parsed from InputStream
     */
    public static Document getDocument(final InputStream input) {
        final Tidy tidy = new Tidy();

        tidy.setErrout(new PrintWriter(new StringWriter()));
        tidy.setNumEntities(true);
        tidy.setXmlOut(true);
        final Document doc = tidy.parseDOM(input, null);
        // tidy.pprint(doc, System.out);

        return doc;
    }
}
