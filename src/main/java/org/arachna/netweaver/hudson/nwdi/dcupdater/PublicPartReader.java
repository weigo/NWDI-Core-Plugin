/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.util.Stack;

import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.xml.AbstractDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Reader for PublicPart descriptions.
 * 
 * @author Dirk Weigenand
 */
final class PublicPartReader extends AbstractDefaultHandler {
    /**
     * element 'caption'.
     */
    private static final String CAPTION = "caption";

    /**
     * element 'description'.
     */
    private static final String DESCRIPTION = "description";

    /**
     * element 'name'.
     */
    private static final String NAME = "name";

    /**
     * element 'public-part'.
     */
    private static final String PUBLIC_PART = "public-part";

    /**
     * stack for keeping track of parent elements.
     */
    private final Stack<String> parents = new Stack<String>();

    /**
     * name of current public part.
     */
    private String publicPartName;

    /**
     * description of current public part.
     */
    private String description;

    /**
     * caption of current public part.
     */
    private String caption;

    /**
     * current public part.
     */
    private PublicPart publicPart;

    /**
     * Create an instance of a <code>PublicPartReader</code>.
     */
    public PublicPartReader() {
        super();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        final String currentParent = getCurrentParent();

        if (PUBLIC_PART.equals(currentParent)) {
            if (NAME.equals(localName)) {
                this.publicPartName = this.getText();
            }
            else if (DESCRIPTION.equals(localName)) {
                this.description = this.getText();
            }
            else if (CAPTION.equals(localName)) {
                this.caption = this.getText();
            }
        }
        else if (PUBLIC_PART.equals(localName)) {
            this.publicPart = new PublicPart(this.publicPartName, this.caption, this.description);
        }
    }

    /**
     * Returns the current parent element.
     * 
     * @return the current parent element.
     */
    private String getCurrentParent() {
        this.parents.pop();
        String currentParent = "";

        if (!this.parents.isEmpty()) {
            currentParent = this.parents.peek();
        }
        return currentParent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
        throws SAXException {
        this.parents.push(localName);
    }

    /**
     * Returns the public part just read from the '.pp' configuration file.
     * 
     * @return the public part just read from the '.pp' configuration file.
     */
    public PublicPart getPublicPart() {
        return this.publicPart;
    }
}
