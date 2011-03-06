/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.xml.XmlReaderHelper;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Update development components with information read from the on disk
 * representation of those DCs (i.e. .confdef, .dcdef, project properties and
 * various JEE configuration files).
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentUpdater {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(DevelopmentComponentUpdater.class.getName());

    /**
     * template for path to the current DCs _comp folder.
     */
    private static final String LOCATION_TEMPLATE = "%s/DCs/%s/%s/_comp";

    /**
     * Folder where development configuration is to be found.
     */
    private final String location;

    /**
     * List of development components to be updated.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * development component currently processed.
     */
    private DevelopmentComponent currentComponent;

    /**
     * base directory for currently processed development component.
     */
    private String componentBase;

    /**
     * Create an instance of <code>DevelopmentComponentUpdater</code>.
     * 
     * @param location
     *            path to folder where development components are situated.
     * @param dcFactory
     *            registry for development components.
     */
    public DevelopmentComponentUpdater(final String location, final DevelopmentComponentFactory dcFactory) {
        this.location = location;
        this.dcFactory = dcFactory;
    }

    /**
     * Loops through all components and updates information read from dc tool
     * with information from file system (i.e. configuration data of DCs:
     * .dcdef, Project.wdproperties, etc.)
     */
    public void execute() {
        for (final DevelopmentComponent component : this.dcFactory.getAll()) {
            this.currentComponent = component;

            this.componentBase = getComponentBaseLocation();
            final File config = this.getDevelopmentComponentConfigurationFile();

            if (config.exists()) {
                updateCurrentComponent(config);
                readPublicParts();
                readProperties();
            }
            else {
                LOGGER.log(Level.INFO, this.currentComponent.getName()
                    + " does not exist in development configuration " + this.location + "!");
            }
        }
    }

    /**
     * Get a {@link XMLReader} object. Throws a <code>RuntimeException</code> if
     * noone could be created.
     * 
     * @return a new <code>XMLReader</code> object.
     */
    private XMLReader getXMLReader() {
        XMLReader reader = null;

        try {
            reader = XMLReaderFactory.createXMLReader();
        }
        catch (final SAXException e) {
            throw new RuntimeException(e);
        }

        return reader;
    }

    /**
     * Update the current development component with information read from its
     * development component configuration file.
     * 
     * @param config
     *            configuration file to be read for updating the component.
     */
    private void updateCurrentComponent(final File config) {
        FileReader configReader = null;

        try {
            configReader = new FileReader(config);
            new XmlReaderHelper(new DcDefinitionReader(this.currentComponent)).parse(configReader);
        }
        catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "The configuration file for " + this.currentComponent.getVendor() + ":"
                + this.currentComponent.getName() + " could not be found!", e);
        }
        catch (final SAXException e) {
            LOGGER.log(Level.SEVERE, "The configuration file for " + this.currentComponent.getVendor() + ":"
                + this.currentComponent.getName() + " could not be parsed!", e);
        }
        finally {
            try {
                if (configReader != null) {
                    configReader.close();
                }
            }
            catch (final IOException e) {
                LOGGER.log(Level.WARNING, "There was an error closing " + config.getAbsolutePath() + "!", e);
            }
        }
    }

    /**
     * Read additional properties from configurations files specific to the type
     * of development component.
     */
    private void readProperties() {
        AbstractComponentConfigurationReader reader = null;

        // TODO: Replace TypeCode with...
        if (DevelopmentComponentType.WebDynpro.equals(this.currentComponent.getType())) {
            reader = new WebDynproProjectPropertiesReader(this.componentBase);
        }
        else if (DevelopmentComponentType.PortalApplicationModule.equals(this.currentComponent.getType())
            || DevelopmentComponentType.PortalApplicationStandalone.equals(this.currentComponent.getType())) {
            reader = new PortalApplicationConfigurationReader(this.componentBase);
        }

        if (reader != null) {
            this.currentComponent.addAll(reader.read());
        }
    }

    /**
     * Read the public parts of the current development component and add them
     * to it.
     */
    private void readPublicParts() {
        final PublicPartsReader ppReader = new PublicPartsReader(this.componentBase);

        // FIXME: DevelopmentComponent should provide an
        // addAll(Collection<PublicPart>) operation.
        for (final PublicPart part : ppReader.read()) {
            this.currentComponent.add(part);
        }
    }

    /**
     * Get the configuration file object.
     * 
     * @return the configuration file object.
     */
    private File getDevelopmentComponentConfigurationFile() {
        return new File(this.componentBase + File.separatorChar + ".dcdef");
    }

    /**
     * Get the base path of the current component (its '_comp' folder).
     * 
     * @return the base path of the current component (its '_comp' folder).
     */
    protected String getComponentBaseLocation() {
        // "%s/DCs/%s/%s/_comp"
        return String.format(LOCATION_TEMPLATE, this.location, this.currentComponent.getVendor(),
            this.currentComponent.getName()).replace('/', File.separatorChar);
    }
}