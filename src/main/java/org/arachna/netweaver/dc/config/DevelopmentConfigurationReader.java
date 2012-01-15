/**
 *
 */
package org.arachna.netweaver.dc.config;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.xml.AbstractDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Reader for a {@link DevelopmentConfiguration} persisted to XML.
 * 
 * @author Dirk Weigenand
 */
public class DevelopmentConfigurationReader extends AbstractDefaultHandler {
    /**
     * constant for tag 'option'.
     */
    private static final String OPTION = "option";
    /**
     * constant for tag 'compartment'.
     */
    private static final String COMPARTMENT = "compartment";
    /**
     * constant for tag 'build-variant'.
     */
    private static final String BUILD_VARIANT = "build-variant";
    /**
     * constant for tag 'value'.
     */
    private static final String VALUE = "value";
    /**
     * constant for tag 'location'.
     */
    private static final String LOCATION = "location";
    /**
     * constant for tag 'description'.
     */
    private static final String DESCRIPTION = "description";
    /**
     * constant for tag 'caption'.
     */
    private static final String CAPTION = "caption";
    /**
     * constant for tag 'name'.
     */
    private static final String NAME = "name";

    /**
     * constant for tag 'development-configuration'.
     */
    private static final String DEVELOPMENT_CONFIGURATION = "development-configuration";

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory developmentComponentFactory;

    /**
     * development configuration to read.
     */
    private DevelopmentConfiguration currentConfiguration;

    /**
     * Reader for the current compartment.
     */
    private CompartmentReader compartmentReader;

    /**
     * Build-Variant.
     */
    private BuildVariant buildVariant;

    /**
     * Create a new <code>DevelopmentConfigurationReader</code> instance. Use
     * the given {@link DevelopmentComponentFactory} to register any
     * {@link DevelopmentComponent}s read in the process of reading the
     * development configuration.
     * 
     * @param developmentComponentFactory
     *            registry for development components
     */
    public DevelopmentConfigurationReader(final DevelopmentComponentFactory developmentComponentFactory) {
        super();
        this.developmentComponentFactory = developmentComponentFactory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String qName) throws SAXException {
        if (compartmentReader != null) {
            currentConfiguration.addAll(compartmentReader.getCompartments());
        }
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
        if (DEVELOPMENT_CONFIGURATION.equals(localName)) {
            currentConfiguration = new DevelopmentConfiguration(attributes.getValue(NAME));
            currentConfiguration.setCaption(attributes.getValue(CAPTION));
            currentConfiguration.setDescription(attributes.getValue(DESCRIPTION));
            currentConfiguration.setLocation(attributes.getValue(LOCATION));
        }
        else if (COMPARTMENT.equals(localName)) {
            compartmentReader = new CompartmentReader(getXmlReader(), this, developmentComponentFactory);
            getXmlReader().setContentHandler(compartmentReader);
            compartmentReader.startElement(uri, localName, qName, attributes);
        }
        else if (BUILD_VARIANT.equals(localName)) {
            buildVariant = new BuildVariant(attributes.getValue(NAME));
            currentConfiguration.setBuildVariant(buildVariant);
        }
        else if (OPTION.equals(localName)) {
            buildVariant.addBuildOption(attributes.getValue(NAME), attributes.getValue(VALUE));
        }
    }

    /**
     * Returns the development configuration.
     * 
     * @return the developmentConfiguration
     */
    public final DevelopmentConfiguration getDevelopmentConfiguration() {
        return currentConfiguration;
    }
}
