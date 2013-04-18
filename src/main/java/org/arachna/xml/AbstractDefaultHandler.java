/**
 *
 */
package org.arachna.xml;

import org.apache.commons.lang.StringEscapeUtils;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Base class for readers for the various configuration files.
 * 
 * @author Dirk Weigenand
 * @deprecated Use Digester!
 */
@Deprecated
public abstract class AbstractDefaultHandler extends DefaultHandler {
    /**
     * the XMLReader object to use reading the configuration files.
     */
    private XMLReader xmlReader;

    /**
     * the parent handler when work is divided between different master/detail
     * handlers.
     */
    private final DefaultHandler parent;

    /**
     * buffer for accumulating text from {@see #characters(char[], int, int)}
     * events.
     */
    private final StringBuffer text = new StringBuffer();

    /**
     * Create an instance of an <code>DefaultHandler</code> using the given
     * parent {@link AbstractDefaultHandler} object.
     * 
     * @param parent
     *            the parent <code>AbstractDefaultHandler</code> to give control
     *            back when finished handling SAX events.
     */
    public AbstractDefaultHandler(final DefaultHandler parent) {
        this.parent = parent;
    }

    /**
     * Create an instance of an {@link AbstractDefaultHandler}.
     * 
     * This instance does not have a parent handler.
     */
    public AbstractDefaultHandler() {
        this(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    @Override
    public final void characters(final char[] ch, final int start, final int length) throws SAXException {
        text.append(new String(ch, start, length));
    }

    /**
     * Get the text that has been accumulated.
     * 
     * The buffer used will be reset by this call.
     * 
     * @return accumulated text.
     */
    protected final String getText() {
        final String value = text.toString().trim();
        text.setLength(0);

        return StringEscapeUtils.unescapeXml(value);
    }

    /**
     * Returns the xmlReader used for parsing the XML.
     * 
     * @return the xmlReader used for parsing the XML.
     */
    public final XMLReader getXmlReader() {
        return xmlReader;
    }

    /**
     * Set the {@link XmlReader} to use for parsing.
     * 
     * @param xmlReader
     *            the xmlReader to set
     */
    public final void setXmlReader(final XMLReader xmlReader) {
        this.xmlReader = xmlReader;
    }

    /**
     * Returns the parent {@link DefaultHandler}.
     * 
     * @return the parent {@link DefaultHandler}.
     */
    protected final DefaultHandler getParent() {
        return parent;
    }
}
