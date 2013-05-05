/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.arachna.netweaver.dc.types.BuildOption;
import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Reader for development configuration (<code>.confdef</code>) files.
 * 
 * @author Dirk Weigenand
 */
public class ConfDefReader {
    /**
     * 'yes' attribute value.
     */
    private static final String YES = "yes";

    /**
     * Factory for {@link BuildVariant} objects.
     */
    private final BuildVariantFactory buildVariantFactory = new BuildVariantFactory();

    /**
     * Update the given development component from the given
     * <code>portalapp.xml</code> file.
     * 
     * @param reader
     *            reader object for reading the <code>portalapp.xml</code> of
     *            the given portal component.
     * @return the development configuration object just read.
     */
    public DevelopmentConfiguration execute(final Reader reader) {
        try {
            final DigesterLoader digesterLoader = DigesterLoader.newLoader(new DevelopmentConfigurationModule());
            final Digester digester = digesterLoader.newDigester();

            final DevelopmentConfiguration config = (DevelopmentConfiguration)digester.parse(reader);

            config.setBuildVariant(buildVariantFactory.findBuildVariantRequiredForActivation(config
                .getCompartments(CompartmentState.Source)));

            return config;
        }
        catch (final SAXException e) {
            throw new IllegalStateException(e);
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Rules for parsing a <code>.confdef</code> development configuration file.
     * 
     * @author Dirk Weigenand
     */
    class DevelopmentConfigurationModule extends AbstractRulesModule {
        @Override
        protected void configure() {
            forPattern("configuration").factoryCreate().usingFactory(new DevelopmentConfigurationFactory());
            forPattern("configuration/config-description").setBeanProperty().withName("description");
            forPattern("configuration/build-server").setBeanProperty().withName("buildServer");
            forPattern("configuration/sc-compartments/sc-compartment").factoryCreate()
                .usingFactory(new CompartmentFactory()).then().setNext("add");
            forPattern("configuration/sc-compartments/sc-compartment/source-state/repository").factoryCreate()
                .usingFactory(new DtrUrlFactory()).then().setNext("setDtrUrl");
            forPattern("configuration/sc-compartments/sc-compartment/source-state/inactive-location").setBeanProperty()
                .withName("inactiveLocation");
            forPattern("configuration/sc-compartments/sc-compartment/dependencies/used-compartment")
                .callMethod("addUsedCompartment").withParamTypes(String.class).withParamCount(1)
                .usingElementBodyAsArgument();
            forPattern("configuration/sc-compartments/sc-compartment/build-variants/build-variant").factoryCreate()
                .usingFactory(buildVariantFactory).then().setNext("add");
            forPattern(
                "configuration/sc-compartments/sc-compartment/build-variants/build-variant/build-options/build-option")
                .factoryCreate().usingFactory(new BuildOptionFactory()).then().setNext("add");
            forPattern(
                "configuration/sc-compartments/sc-compartment/build-variants/build-variant/build-options/build-option/option-value")
                .setBeanProperty().withName("value");
        }
    }

    /**
     * Create {@link DevelopmentConfiguration} objects from attributes of
     * <code>sc-compartment</code> elements.
     * 
     * @author Dirk Weigenand
     */
    private static class DevelopmentConfigurationFactory extends
        AbstractObjectCreationFactory<DevelopmentConfiguration> {
        @Override
        public DevelopmentConfiguration createObject(final Attributes attributes) throws Exception {
            final DevelopmentConfiguration config = new DevelopmentConfiguration(attributes.getValue("name"));

            config.setCmsUrl(attributes.getValue("cms-url"));
            config.setVersion(Integer.valueOf(attributes.getValue("config-version")).toString());
            config.setCaption(attributes.getValue("caption"));

            return config;
        }
    }

    /**
     * Create {@link Compartment} objects from attributes of
     * <code>sc-compartment</code> elements.
     * 
     * @author Dirk Weigenand
     */
    private static class CompartmentFactory extends AbstractObjectCreationFactory<Compartment> {
        @Override
        public Compartment createObject(final Attributes attributes) throws Exception {
            final CompartmentState state =
                YES.equals(attributes.getValue("archive-state")) ? CompartmentState.Archive : CompartmentState.Source;

            return Compartment.create(attributes.getValue("name"), state);
        }
    }

    /**
     * Extract the dtr URL from attributes of <code>repository</code> elements.
     * 
     * @author Dirk Weigenand
     */
    private static class DtrUrlFactory extends AbstractObjectCreationFactory<String> {
        @Override
        public String createObject(final Attributes attributes) throws Exception {
            return attributes.getValue("url");
        }
    }

    /**
     * Create {@link BuildVariant} objects from attributes of
     * <code>build-variant</code> elements.
     * 
     * @author Dirk Weigenand
     */
    private static class BuildVariantFactory extends AbstractObjectCreationFactory<BuildVariant> {
        /**
         * mapping from name to build variants.
         */
        private final Map<String, BuildVariant> buildVariants = new HashMap<String, BuildVariant>();

        /**
         * Create a new {@link BuildVariant} object from the <code>name</code>
         * and <code>required-for-activation</code> attributes of a
         * <code>build-variant</code> element.
         * 
         * @param attributes
         *            the attributes of the <code>build-variant</code> element.
         * @return a new build variant object with the given name indicating
         *         whether it is required for activation or not.
         */
        @Override
        public BuildVariant createObject(final Attributes attributes) throws Exception {
            BuildVariant variant = buildVariants.get(attributes.getValue("name"));

            if (null == variant) {
                variant =
                    new BuildVariant(attributes.getValue("name"), YES.equals(attributes
                        .getValue("required-for-activation")));
                buildVariants.put(variant.getName(), variant);
            }

            return variant;
        }

        /**
         * Find the build variant to use as default.
         * 
         * @param sourceCompartments
         *            collection of compartments in source state.
         * @return the build variant to use as default (the one that is used for
         *         building in the CBS).
         */
        private BuildVariant findBuildVariantRequiredForActivation(final Collection<Compartment> sourceCompartments) {
            BuildVariant defaultBuildVariant = null;

            for (final BuildVariant variant : buildVariants.values()) {
                if (variant.isRequiredForActivation()) {
                    defaultBuildVariant = variant;
                }
            }

            return defaultBuildVariant;
        }
    }

    /**
     * Extract a build option name from attributes of <code>build-option</code>
     * elements.
     * 
     * @author Dirk Weigenand
     */
    private static class BuildOptionFactory extends AbstractObjectCreationFactory<BuildOption> {
        @Override
        public BuildOption createObject(final Attributes attributes) throws Exception {
            return new BuildOption(attributes.getValue("name"));
        }
    }
}
