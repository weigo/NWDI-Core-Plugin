/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.confdef;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Reader for <code>.confdef</code> configuration files for development
 * configurations.
 * 
 * @author Dirk Weigenand
 */
public final class ConfDefReader extends AbstractDefaultHandler {
    /**
     * Tag name for the 'build-server' element.
     */
    private static final String BUILD_SERVER = "build-server";

    /**
     * Attribute for URL to CMS server.
     */
    private static final String CMS_URL = "cms-url";

    /**
     * tag name for the 'caption' tag.
     */
    private static final String CAPTION = "caption";

    /**
     * tag name for the 'name' tag.
     */
    private static final String NAME = "name";

    /**
     * tag name for the 'configuration' tag.
     */
    private static final String CONFIGURATION = "configuration";

    /**
     * tag name for the 'sc-compartments' tag.
     */
    private static final String SC_COMPARTMENTS = "sc-compartments";

    /**
     * tag name for the 'config-description' tag.
     */
    private static final String CONFIG_DESCRIPTION = "config-description";

    /**
     * the development configuration that has been read.
     */
    private DevelopmentConfiguration developmentConfiguration;

    /**
     * reader for the compartments contained in this development configuration.
     */
    private CompartmentReader compartmentReader;

    /**
     * Create an instance of a <code>ConfDefReader</code> using the given
     * {@link XMLReader}.
     * 
     * @param xmlReader
     *            the <code>XMLReader</code> to be used to read the
     *            <code>.confdef</code> configuration file.
     */
    public ConfDefReader(final XMLReader xmlReader) {
        super(xmlReader, null);
        this.getXmlReader().setContentHandler(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.xml.sax.ContentHandler#endElement(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void endElement(final String uri, final String localName, final String name) throws SAXException {
        if (CONFIG_DESCRIPTION.equals(localName)) {
            this.developmentConfiguration.setDescription(this.getText());
        }
        else if (BUILD_SERVER.equals(localName)) {
            this.developmentConfiguration.setBuildServer(this.getText());
        }
        else if (SC_COMPARTMENTS.equals(localName)) {
            this.developmentConfiguration.addAll(this.compartmentReader.getCompartments());
            this.developmentConfiguration.setBuildVariant(this.compartmentReader.getBuildVariant());
            this.compartmentReader = null;
        }
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
        if (CONFIGURATION.equals(localName)) {
            this.developmentConfiguration = new DevelopmentConfiguration(atts.getValue(NAME));
            this.developmentConfiguration.setCaption(atts.getValue(CAPTION));
            this.developmentConfiguration.setCmsUrl(atts.getValue(CMS_URL));
        }
        else if (SC_COMPARTMENTS.equals(localName)) {
            this.compartmentReader = new CompartmentReader(this.getXmlReader(), this);
            this.getXmlReader().setContentHandler(this.compartmentReader);
        }
    }

    /**
     * Read the development configuration from the given {@link InputStream}.
     * 
     * @param config
     *            the <code>InputStream</code> to read the configuration from.
     * @return the developmentConfiguration just read.
     */
    public DevelopmentConfiguration read(final InputStream config) {
        return read(new InputStreamReader(config));
    }

    /**
     * Read the development configuration from the given {@link Reader}.
     * 
     * @param config
     *            the <code>Reader</code> to read the configuration from.
     * @return the developmentConfiguration just read.
     */
    public DevelopmentConfiguration read(final Reader config) {
        final InputSource input = new InputSource(config);

        try {
            this.getXmlReader().parse(input);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        catch (final SAXException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                config.close();
            }
            catch (final IOException e) {
            }
        }

        return this.developmentConfiguration;
    }
}
