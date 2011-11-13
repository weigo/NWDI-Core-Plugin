/**
 *
 */
package org.arachna.netweaver.dc.config;

import java.io.IOException;
import java.io.Reader;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.xml.AbstractDefaultHandler;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Reader für Entwicklungskonfigurationen.
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
     * Factory für Entwicklungskomponenten.
     */
    private final DevelopmentComponentFactory developmentComponentFactory;

    /**
     * Aktuell einzulesende Entwicklungskonfiguration.
     */
    private DevelopmentConfiguration currentConfiguration;

    /**
     * Reader für aktuelles Compartment.
     */
    private CompartmentReader compartmentReader;

    /**
     * Build-Variante.
     */
    private BuildVariant buildVariant;

    /**
     * Erzeugt einen Reader für Entwicklungskonfigurationen.
     * 
     * @param xmlReader
     *            XMLReader zum Einlesen der Konfigurationen.
     * @param developmentComponentFactory
     *            Factory zum Registrieren der Entwicklungskomponenten
     */
    public DevelopmentConfigurationReader(final XMLReader xmlReader,
        final DevelopmentComponentFactory developmentComponentFactory) {
        super();
        setXmlReader(xmlReader);
        this.developmentComponentFactory = developmentComponentFactory;
        getXmlReader().setContentHandler(this);
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
     * Read development configurations from the given {@link InputStream}.
     * 
     * @param input
     *            the configuration containing the stored development
     *            configurations
     * @return the developmentConfigurations
     * @throws SAXException
     *             any <code>SAXexception</code> that is throw in the underlying
     *             code.
     * @throws IOException
     *             any <code>IOexception</code> that is throw in the underlying
     *             code.
     */
    public final DevelopmentConfiguration read(final Reader input) throws IOException, SAXException {
        getXmlReader().parse(new InputSource(input));

        return currentConfiguration;
    }
}
