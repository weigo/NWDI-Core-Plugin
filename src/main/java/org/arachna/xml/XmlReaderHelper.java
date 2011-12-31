/**
 *
 */
package org.arachna.xml;

import java.io.IOException;
import java.io.Reader;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Helper for using {@link XMLReader}s.
 * 
 * @author Dirk Weigenand
 */
public final class XmlReaderHelper {
    /**
     * Constant for sax driver property.
     */
    private static final String ORG_XML_SAX_DRIVER = "org.xml.sax.driver";

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
        xmlReader.setContentHandler(handler);
        handler.setXmlReader(xmlReader);

        xmlReader.parse(new InputSource(input));
    }

    /**
     * Creates an {@link XMLReader}.
     * 
     * Catch errors when the {@see #ORG_XML_SAX_DRIVER} system property is set
     * to a class that cannot be loaded at runtime. The property will be cleared
     * in this case and the operation retried.
     * 
     * @return a new {@link XMLReader} object
     * @throws SAXException
     *             when creating the reader fails
     */
    private XMLReader createXmlReader() throws SAXException {
        XMLReader xmlReader = null;

        try {
            xmlReader = XMLReaderFactory.createXMLReader();
        }
//CHECKSTYLE:OFF
        catch (final RuntimeException re) {
//CHECKSTYLE:ON
            if (re.getCause() != null && ClassNotFoundException.class.equals(re.getCause().getClass())) {
                final String saxDriverProperty = System.getProperty(ORG_XML_SAX_DRIVER);

                if (saxDriverProperty != null) {
                    System.setProperty(ORG_XML_SAX_DRIVER, null);
                }

                xmlReader = XMLReaderFactory.createXMLReader();
            }
            else {
                throw re;
            }
        }

        return xmlReader;
    }
}
