/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.confdef;

import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Base class for readers for the various configuration files.
 * 
 * @author Dirk Weigenand
 */
public abstract class AbstractDefaultHandler extends DefaultHandler {
    /**
     * the XMLReader object to use reading the configuration files.
     */
    private final XMLReader xmlReader;

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
     * {@link XMLReader} and parent <code>AbstractDefaultHandler</code> object.
     * 
     * @param xmlReader
     *            the {@link XMLReader} used reading.
     * @param parent
     *            the parent <code>AbstractDefaultHandler</code> to give control
     *            back when finished handling SAX events.
     */
    public AbstractDefaultHandler(final XMLReader xmlReader, final DefaultHandler parent) {
        this.xmlReader = xmlReader;
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#characters(char[], int, int)
     */
    @Override
    public final void characters(final char[] ch, final int start, final int length) throws SAXException {
        this.text.append(new String(ch, start, length));
    }

    /**
     * Get the text that has been accumulated.
     * 
     * The buffer used will be reset by this call.
     * 
     * @return accumulated text.
     */
    protected final String getText() {
        final String value = this.text.toString().trim();
        this.text.setLength(0);

        return value;
    }

    /**
     * @return the xmlReader
     */
    public final XMLReader getXmlReader() {
        return this.xmlReader;
    }

    /**
     * @return the parent
     */
    protected final DefaultHandler getParent() {
        return this.parent;
    }
}
