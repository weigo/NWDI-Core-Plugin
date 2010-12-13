/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.File;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Reader for 'ProjectProperties.wdproperties' configuration files.
 * 
 * @author Dirk Weigenand
 */
final class WebDynproProjectPropertiesReader extends AbstractComponentConfigurationReader {
    /**
     * Location of ProjectProperties.wdproperties relative to the component
     * base.
     */
    private static final String CONFIGURATION_LOCATION = "src%cpackages%cProjectProperties.wdproperties";

    /**
     * prefix used to reference portal applications/services.
     */
    private static final String PORTAL_PREFIX = "PORTAL:";

    /**
     * attribute name for name of referenced application/library.
     */
    private static final String LIBRARY_NAME = "libraryName";

    /**
     * tag name for library references.
     */
    private static final String LIBRARY_REFERENCE = "LibraryReference";

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
     * java.lang.String, java.lang.String, org.xml.sax.Attributes)
     */
    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes)
        throws SAXException {
        if (LIBRARY_REFERENCE.equals(localName)) {
            this.addPublicPartReference(attributes.getValue(LIBRARY_NAME).replace(PORTAL_PREFIX, ""));
        }
    }

    /**
     * Create an instance of an <code>WebDynproProjectPropertiesReader</code>.
     * 
     * @param reader
     *            the {@link XMLReader} to use parsing the configuration file.
     * @param componentBase
     *            base directory of development component.
     */
    public WebDynproProjectPropertiesReader(final XMLReader reader, final String componentBase) {
        super(reader, componentBase);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.arachna.netweaver.dc.analyzer.dctool.readers.
     * AbstractComponentConfigurationReader#getConfigurationLocation()
     */
    @Override
    protected String getConfigurationLocation() {
        return String.format(CONFIGURATION_LOCATION, File.separatorChar, File.separatorChar);
    }
}
