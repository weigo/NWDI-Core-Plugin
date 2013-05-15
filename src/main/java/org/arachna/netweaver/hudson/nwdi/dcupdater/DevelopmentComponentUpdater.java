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
import org.arachna.xml.DigesterHelper;
import org.arachna.xml.RulesModuleProducer;

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
        for (final DevelopmentComponent component : dcFactory.getAll()) {
            DcPropertiesReaderDescriptor.update(component, antHelper);
            readPublicParts(component);
        }
    }

    /**
     * Descriptor for configuration files to update a development component
     * from.
     * 
     * @author Dirk Weigenand
     */
    private enum DcPropertiesReaderDescriptor {
        /**
         * Descriptor for DCs of type WebDynpro.
         */
        WebDynpro(DevelopmentComponentType.WebDynpro, new WebDynproProjectPropertiesRulesModuleProducer(),
            "src/packages/ProjectProperties.wdproperties"),

        /**
         * Descriptor for DCs of type Portal Application Module.
         */
        PortalApplicationModule(DevelopmentComponentType.PortalApplicationModule,
            new PortalApplicationConfigurationRulesModuleProducer(), "dist/PORTAL-INF/portalapp.xml"),

        /**
         * Descriptor for DCs of type Portal Standalone Application.
         */
        PortalApplicationStandalone(DevelopmentComponentType.PortalApplicationStandalone,
            new PortalApplicationConfigurationRulesModuleProducer(), "dist/PORTAL-INF/portalapp.xml"),

        /**
         * Special type meaning all development component types.
         */
        All(null, new DcDefinitionRulesModuleProducer(), ".dcdef");

        /**
         * type of development component.
         */
        private final DevelopmentComponentType dcType;

        /**
         * producer for rules to parse the config file.
         */
        private final RulesModuleProducer rulesModuleProducer;

        /**
         * name of configuration file relative to <code>_comp</code> folder.
         */
        private final String configFile;

        /**
         * Create descriptor instance with given type, rules producer and config
         * file name.
         * 
         * @param dcType
         *            type of development component may be null.
         * @param rulesModuleProducer
         *            producer for parsing rules.
         * @param configFile
         *            name of configuration file.
         */
        private DcPropertiesReaderDescriptor(final DevelopmentComponentType dcType,
            final RulesModuleProducer rulesModuleProducer, final String configFile) {
            this.dcType = dcType;
            this.rulesModuleProducer = rulesModuleProducer;
            this.configFile = configFile;
        }

        /**
         * Get a reader for the configuration file.
         * 
         * @param antHelper
         *            use antHelper to determine the exact location in the file
         *            system.
         * @param component
         *            determine path for this component.
         * @return reader for config file.
         * @throws FileNotFoundException
         *             when the configuration file could not be found, i.e. in
         *             NW CE and new releases there is no
         *             <code>ProjectProperties.wdproperties</code> anymore.
         */
        private Reader getConfigFile(final AntHelper antHelper, final DevelopmentComponent component)
            throws FileNotFoundException {
            return new InputStreamReader(
                new FileInputStream(new File(antHelper.getBaseLocation(component), configFile)),
                Charset.forName("UTF-8"));
        }

        /**
         * Update the given development component with the properties defined by
         * this descriptor.
         * 
         * @param antHelper
         *            helper class for extracting file names.
         * @param component
         *            component to update.
         */
        private void update(final AntHelper antHelper, final DevelopmentComponent component) {
            try {
                new DigesterHelper<DevelopmentComponent>(rulesModuleProducer).update(
                    getConfigFile(antHelper, component), component);
            }
            catch (final FileNotFoundException e) {
                // ignore
            }
        }

        /**
         * External interface for updating development components.
         * 
         * @param component
         *            component to update.
         * @param antHelper
         *            helper class for determining file paths.
         */
        static void update(final DevelopmentComponent component, final AntHelper antHelper) {
            for (final DcPropertiesReaderDescriptor descriptor : values()) {
                if (descriptor.dcType != null && descriptor.dcType.equals(component.getType())) {
                    descriptor.update(antHelper, component);
                    break;
                }
            }
        }
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
