/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;

/**
 * Update development components with information read from the on disk
 * representation of those DCs (i.e. .confdef, .dcdef, project properties and
 * various JEE configuration files).
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentUpdater {
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
     * Reader for <code>ProjectProperties.wdproperties</code> files.
     */
    private final ComponentConfigurationReader wdPropertiesReader = new WebDynproProjectPropertiesReader();

    /**
     * Reader for <code>portalapp.xml</code> files.
     */
    private final ComponentConfigurationReader portalApplicationConfigurationReader =
        new PortalApplicationConfigurationReader();

    /**
     * helper class.
     */
    private final AntHelper antHelper;

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
        antHelper = new AntHelper(location, dcFactory);
    }

    /**
     * Loops through all components and updates information read from dc tool
     * with information from file system (i.e. configuration data of DCs:
     * .dcdef, Project.wdproperties, etc.)
     */
    public void execute() {
        final DcDefinitionReader dcDefinitionReader = new DcDefinitionReader();

        for (final DevelopmentComponent component : dcFactory.getAll()) {
            currentComponent = component;
            componentBase = getComponentBaseLocation();

            try {
                dcDefinitionReader.execute(component, getConfigFile(component, ".dcdef"));
            }
            catch (final FileNotFoundException e) {
                // ignore
            }

            readPublicParts();
            readProperties(component);
        }
    }

    /**
     * Read additional properties from configuration files specific to the type
     * of development component.
     * 
     * @param component
     *            development to additional configuration files for.
     */
    private void readProperties(final DevelopmentComponent component) {
        try {
            if (DevelopmentComponentType.WebDynpro.equals(currentComponent.getType())) {
                wdPropertiesReader.execute(component,
                    getConfigFile(component, "src/packages/ProjectProperties.wdproperties"));
            }
            else if (DevelopmentComponentType.PortalApplicationModule.equals(currentComponent.getType())
                || DevelopmentComponentType.PortalApplicationStandalone.equals(currentComponent.getType())) {
                portalApplicationConfigurationReader.execute(component,
                    getConfigFile(component, "dist/PORTAL-INF/portalapp.xml"));
            }
        }
        catch (final FileNotFoundException e) {
            // ignore
        }
    }

    /**
     * Get a reader for the given development component and configuration file
     * name.
     * 
     * @param component
     *            development component to read configuration files for.
     * @param configFile
     *            the configuration file name.
     * @return a reader for the given configuration file name.
     * @throws FileNotFoundException
     *             when the given file could not be found.
     */
    private Reader getConfigFile(final DevelopmentComponent component, final String configFile)
        throws FileNotFoundException {
        final String absolutePath = String.format("%s/%s", antHelper.getBaseLocation(component), configFile);
        return new InputStreamReader(new FileInputStream(absolutePath), Charset.forName("UTF-8"));
    }

    /**
     * Read the public parts of the current development component and add them
     * to it.
     */
    private void readPublicParts() {
        currentComponent.setPublicParts(new PublicPartsReader(componentBase).read());
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
