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
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.xml.DigesterHelper;
import org.arachna.xml.RulesModuleProducer;

/**
 * Update development components with information read from the on disk representation of those DCs (i.e. .confdef, .dcdef, project
 * properties and various JEE configuration files).
 *
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentUpdater {
    /**
     * helper class.
     */
    private final AntHelper antHelper;

    /**
     * Create an instance of <code>DevelopmentComponentUpdater</code>.
     *
     * @param antHelper
     *            Helper class for gathering information development components.
     */
    public DevelopmentComponentUpdater(final AntHelper antHelper) {
        this.antHelper = antHelper;
    }

    /**
     * Loops through all components and updates information read from dc tool with information from file system (i.e. configuration data of
     * DCs: .dcdef, Project.wdproperties, etc.)
     *
     * @param components
     *            collection of development components to update.
     */
    public void execute(final Collection<DevelopmentComponent> components) {
        for (final DevelopmentComponent component : components) {
            DcPropertiesReaderDescriptor.All.update(antHelper, component);

            for (final DcPropertiesReaderDescriptor descriptor : DcPropertiesReaderDescriptor.values()) {
                if (descriptor.dcType != null && descriptor.dcType.equals(component.getType())) {
                    descriptor.update(antHelper, component);
                    break;
                }
            }

            readPublicParts(component);
        }
    }

    /**
     * Descriptor for configuration files to update a development component from.
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
            PortalApplicationModule(DevelopmentComponentType.PortalApplicationModule, new PortalApplicationConfigurationRulesModuleProducer(),
                "dist/PORTAL-INF/portalapp.xml"),

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
         * Create descriptor instance with given type, rules producer and config file name.
         *
         * @param dcType
         *            type of development component may be null.
         * @param rulesModuleProducer
         *            producer for parsing rules.
         * @param configFile
         *            name of configuration file.
         */
        private DcPropertiesReaderDescriptor(final DevelopmentComponentType dcType, final RulesModuleProducer rulesModuleProducer,
            final String configFile) {
            this.dcType = dcType;
            this.rulesModuleProducer = rulesModuleProducer;
            this.configFile = configFile;
        }

        /**
         * Get a reader for the configuration file.
         *
         * @param config
         *            configuration file object.
         * @return reader for configuration file.
         * @throws FileNotFoundException
         *             when the configuration file could not be found, i.e. in NW CE and new releases there is no
         *             <code>ProjectProperties.wdproperties</code> anymore.
         */
        private Reader getConfigFile(final File config) throws FileNotFoundException {
            return new InputStreamReader(new FileInputStream(config), Charset.forName("UTF-8"));
        }

        /**
         * Update the given development component with the properties defined by this descriptor.
         *
         * @param antHelper
         *            helper class for extracting file names.
         * @param component
         *            component to update.
         */
        private void update(final AntHelper antHelper, final DevelopmentComponent component) {
            final File config = new File(antHelper.getBaseLocation(component), configFile);

            try {
                new DigesterHelper<DevelopmentComponent>(rulesModuleProducer).update(getConfigFile(config), component);
            }
            catch (final FileNotFoundException e) {
                // ignore
            }
            catch (final IllegalStateException ise) {
                Logger.getLogger(getClass().getName()).log(Level.WARNING,
                    String.format("Error updating DC %s using %s!", component.getNormalizedName("~"), config.getAbsolutePath()), ise);
            }
        }
    }

    /**
     * Read the public parts of the current development component and add them to it.
     *
     * @param component
     *            development component to determine public parts for.
     */
    private void readPublicParts(final DevelopmentComponent component) {
        component.setPublicParts(new PublicPartsReader(antHelper.getBaseLocation(component)).read());
    }
}
