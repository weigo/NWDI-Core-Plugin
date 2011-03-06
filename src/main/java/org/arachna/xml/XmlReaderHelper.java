/**
 *
 */
package org.arachna.xml;

import java.io.IOException;
import java.io.Reader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Helper for using {@link XMLReader}s.
 * 
 * @author Dirk Weigenand
 */
public final class XmlReaderHelper {
    /**
     * handler to use for handling SAX events.
     */
    private final AbstractDefaultHandler handler;

    /**
     * Create an instance of {@link XmlReaderHelper} using the given
     * {@link DefaultHandler}.
     * 
     * @param handler
     *            handler to use for handling SAX events.
     */
    public XmlReaderHelper(final AbstractDefaultHandler handler) {
        this.handler = handler;

    }

    /**
     * Parse the XML read from the given reader.
     * 
     * @param input
     *            {@link Reader} to use reading the given XML.
     * @throws SAXException
     *             when parsing the XML input fails.
     * @throws IOException
     *             when reading the XML input fails.
     */
    public void parse(final Reader input) throws IOException, SAXException {
        final XMLReader xmlReader = createXmlReader();
        xmlReader.setContentHandler(this.handler);
        this.handler.setXmlReader(xmlReader);

        xmlReader.parse(new InputSource(input));
    }

    /**
     * @return
     * @throws SAXException
     */
    private XMLReader createXmlReader() throws SAXException {
        XMLReader xmlReader = null;

        try {
            xmlReader = XMLReaderFactory.createXMLReader();
        }
        catch (final RuntimeException re) {
            if (re.getCause() != null && ClassNotFoundException.class.equals(re.getCause().getClass())) {
                final String saxDriverProperty = System.getProperty("org.xml.sax.driver");

                if (saxDriverProperty != null) {
                    System.getProperty("org.xml.sax.driver", null);
                }

                xmlReader = XMLReaderFactory.createXMLReader();
            }
        }

        return xmlReader;
    }
}
