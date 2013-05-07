/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.xml.DigesterHelper;

/**
 * Update development components with information read from the on disk
 * representation of those DCs (i.e. .confdef, .dcdef, project properties and
 * various JEE configuration files).
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentUpdater {
    /**
     * List of development components to be updated.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Reader for <code>ProjectProperties.wdproperties</code> files.
     */
    private final DigesterHelper<DevelopmentComponent> wdPropertiesReader = new DigesterHelper<DevelopmentComponent>(
        new WebDynproProjectPropertiesRulesModuleProducer());

    /**
     * Reader for <code>portalapp.xml</code> files.
     */
    private final DigesterHelper<DevelopmentComponent> portalApplicationConfigurationReader =
        new DigesterHelper<DevelopmentComponent>(new PortalApplicationConfigurationRulesModuleProducer());

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
        this.dcFactory = dcFactory;
        antHelper = new AntHelper(location, dcFactory);
    }

    /**
     * Loops through all components and updates information read from dc tool
     * with information from file system (i.e. configuration data of DCs:
     * .dcdef, Project.wdproperties, etc.)
     */
    public void execute() {
        final DigesterHelper<DevelopmentComponent> digesterHelper =
            new DigesterHelper<DevelopmentComponent>(new DcDefinitionRulesModuleProducer());

        for (final DevelopmentComponent component : dcFactory.getAll()) {
            try {
                digesterHelper.update(getConfigFile(component, ".dcdef"), component);
            }
            catch (final FileNotFoundException e) {
                // ignore
            }

            readPublicParts(component);
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
            if (DevelopmentComponentType.WebDynpro.equals(component.getType())) {
                wdPropertiesReader.update(getConfigFile(component, "src/packages/ProjectProperties.wdproperties"),
                    component);
            }
            else if (DevelopmentComponentType.PortalApplicationModule.equals(component.getType())
                || DevelopmentComponentType.PortalApplicationStandalone.equals(component.getType())) {
                portalApplicationConfigurationReader.update(getConfigFile(component, "dist/PORTAL-INF/portalapp.xml"),
                    component);
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
     * 
     * @param component
     *            development component to determine public parts for.
     */
    private void readPublicParts(final DevelopmentComponent component) {
        component.setPublicParts(new PublicPartsReader(antHelper.getBaseLocation(component)).read());
    }
}
