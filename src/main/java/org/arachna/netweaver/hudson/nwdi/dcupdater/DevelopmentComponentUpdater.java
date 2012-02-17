/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.xml.XmlReaderHelper;
import org.xml.sax.SAXException;

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
        for (final DevelopmentComponent component : dcFactory.getAll()) {
            currentComponent = component;

            componentBase = getComponentBaseLocation();
            final File config = getDevelopmentComponentConfigurationFile();

            if (config.exists()) {
                updateCurrentComponent(config);
                readPublicParts();
                readProperties();
            }
            else {
                LOGGER.log(Level.INFO, currentComponent.getName() + " does not exist in development configuration "
                    + location + "!");
            }
        }
    }

    /**
     * Update the current development component with information read from its
     * development component configuration file.
     * 
     * @param config
     *            configuration file to be read for updating the component.
     */
    private void updateCurrentComponent(final File config) {
        Reader configReader = null;

        try {
            configReader = new InputStreamReader(new FileInputStream(config), "UTF-8");
            new XmlReaderHelper(new DcDefinitionReader(currentComponent)).parse(configReader);
        }
        catch (final IOException e) {
            LOGGER.log(Level.SEVERE, "The configuration file for " + currentComponent.getVendor() + ":"
                + currentComponent.getName() + " could not be found!", e);
        }
        catch (final SAXException e) {
            LOGGER.log(Level.SEVERE, "The configuration file for " + currentComponent.getVendor() + ":"
                + currentComponent.getName() + " could not be parsed!", e);
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
        if (DevelopmentComponentType.WebDynpro.equals(currentComponent.getType())) {
            reader = new WebDynproProjectPropertiesReader(componentBase);
        }
        else if (DevelopmentComponentType.PortalApplicationModule.equals(currentComponent.getType())
            || DevelopmentComponentType.PortalApplicationStandalone.equals(currentComponent.getType())) {
            reader = new PortalApplicationConfigurationReader(componentBase);
        }

        if (reader != null) {
            for (PublicPartReference newPpRef : reader.read()) {
                if (!currentComponent.hasRuntimeReference(newPpRef)) {
                    currentComponent.add(newPpRef);
                }
            }
        }
    }

    /**
     * Read the public parts of the current development component and add them
     * to it.
     */
    private void readPublicParts() {
        this.currentComponent.setPublicParts(new PublicPartsReader(componentBase).read());
    }

    /**
     * Get the configuration file object.
     * 
     * @return the configuration file object.
     */
    private File getDevelopmentComponentConfigurationFile() {
        return new File(componentBase + File.separatorChar + ".dcdef");
    }

    /**
     * Get the base path of the current component (its '_comp' folder).
     * 
     * @return the base path of the current component (its '_comp' folder).
     */
    protected String getComponentBaseLocation() {
        return String.format(LOCATION_TEMPLATE, location, currentComponent.getVendor(), currentComponent.getName())
            .replace('/', File.separatorChar);
    }
}
