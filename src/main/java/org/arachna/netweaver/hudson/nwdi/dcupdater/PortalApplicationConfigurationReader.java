/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.File;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Read a portal applications 'portalapp.xml' configuration file and extract
 * information on its sharing references.
 * 
 * @author Dirk Weigenand
 */
final class PortalApplicationConfigurationReader extends AbstractComponentConfigurationReader {
    /**
     * comma constant.
     */
    private static final String COMMA = ",";

    /**
     * constant for attribute 'value'.
     */
    private static final String VALUE = "value";

    /**
     * constant for attribute 'name'.
     */
    private static final String NAME = "name";

    /**
     * constant for attribute 'SharingReference'.
     */
    private static final String SHARING_REFERENCE_ATTRIBUTE_NAME = "SharingReference";

    /**
     * constant for element name 'property'.
     */
    private static final String PROPERTY_TAG = "property";
    /**
     * Prefix to references to J2EE library applications (development components
     * of type J2EE Library).
     */
    private static final String SAPJ2EE_LIBRARY = "SAPJ2EE::library:";

    /**
     * Create an instance of <code>PortalApplicationConfigurationReader</code>
     * using the given <code>XMLReader</code> object as parser for the
     * configuration file.
     * 
     * @param componentBase
     *            base directory of development component.
     */
    public PortalApplicationConfigurationReader(final String componentBase) {
        super(componentBase);
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
        if (PROPERTY_TAG.equals(localName) && SHARING_REFERENCE_ATTRIBUTE_NAME.equals(attributes.getValue(NAME))) {
            for (final String reference : attributes.getValue(VALUE).split(COMMA)) {
                final String ref = reference.trim();

                if (ref.startsWith(PortalApplicationConfigurationReader.SAPJ2EE_LIBRARY)) {
                    this.addPublicPartReference(ref.substring(PortalApplicationConfigurationReader.SAPJ2EE_LIBRARY
                        .length()));
                }
                else {
                    this.addPublicPartReference(ref);
                }
            }
        }
    }

    /**
     * Return the path to the 'portalapp.xml' configuration file relative to the
     * components base folder ('_comp').
     * 
     * @return the path to the 'portalapp.xml' configuration file relative to
     *         the components base folder ('_comp').
     */
    @Override
    protected String getConfigurationLocation() {
        return String.format("dist%cPORTAL-INF%cportalapp.xml", File.separatorChar, File.separatorChar);
    }
}