/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.util.HashSet;
import java.util.Set;

import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.netweaver.hudson.nwdi.confdef.AbstractDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Handler for the 'dependencies' section/element in a '.dcdef' configuration
 * file.
 * 
 * @author Dirk Weigenand
 */
class DcDependenciesReader extends AbstractDefaultHandler {
    /**
     * element name 'at-runtime'.
     */
    private static final String AT_RUNTIME = "at-runtime";

    /**
     * element name 'at-build-time'.
     */
    private static final String AT_BUILD_TIME = "at-build-time";

    /**
     * element name 'pp-ref'.
     */
    private static final String PP_REF = "pp-ref";

    /**
     * attribute name 'vendor'.
     */
    private static final String VENDOR = "vendor";

    /**
     * attribute name 'name'.
     */
    private static final String NAME = "name";

    /**
     * element name 'dependency'.
     */
    private static final String DEPENDENCY = "dependency";

    /**
     * element name 'dependencies'.
     */
    private static final String DEPENDENCIES = "dependencies";

    /**
     * set of references to public parts of other development components.
     */
    private final Set<PublicPartReference> usedComponents = new HashSet<PublicPartReference>();

    private String referencedComponentName;

    private String referencedComponentVendor;

    private String referencedComponentPublicPart;

    private boolean atRunTime;

    private boolean atBuildTime;

    /**
     * Create an instance of <code>DcDependenciesReader</code> using the given
     * {@link XMLReader} and {@link DefaultHandler}.
     * 
     * @param xmlReader
     *            <code>XMLReader</code> to generate SAX events from source
     *            being read.
     * @param handler
     *            <code>DefaultHandler</code> parent handler to give control
     *            back to after parsing the 'dependencies' section.
     */
    public DcDependenciesReader(final XMLReader xmlReader, final DefaultHandler handler) {
        super(xmlReader, handler);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if (DEPENDENCIES.equals(localName)) {
            this.getXmlReader().setContentHandler(this.getParent());
            this.getParent().endElement(uri, localName, name);
        }
        else if (DEPENDENCY.equals(localName)) {
            final PublicPartReference publicPartReference =
                new PublicPartReference(this.referencedComponentVendor, this.referencedComponentName,
                    this.referencedComponentPublicPart);
            publicPartReference.setAtBuildTime(this.atBuildTime);
            publicPartReference.setAtRunTime(this.atRunTime);

            this.usedComponents.add(publicPartReference);
        }
        else if (NAME.equals(localName)) {
            this.referencedComponentName = this.getText();
        }
        else if (VENDOR.equals(localName)) {
            this.referencedComponentVendor = this.getText();
        }
        else if (PP_REF.equals(localName)) {
            this.referencedComponentPublicPart = this.getText();
        }

        this.getText();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, final String name, final Attributes atts)
        throws SAXException {
        if (DEPENDENCY.equals(localName)) {
            this.referencedComponentVendor = "";
            this.referencedComponentName = "";
            this.referencedComponentPublicPart = "";
            this.atBuildTime = false;
            this.atRunTime = false;
        }
        else if (AT_BUILD_TIME.equals(localName)) {
            this.atBuildTime = true;
        }
        else if (AT_RUNTIME.equals(localName)) {
            this.atRunTime = true;
        }
    }

    /**
     * Return the collection of used components extracted from the '.dcdef'
     * configuration file.
     * 
     * @return collection of used components extracted from the '.dcdef'
     *         configuration file.
     */
    public final Set<PublicPartReference> getUsedComponents() {
        return usedComponents;
    }
}
