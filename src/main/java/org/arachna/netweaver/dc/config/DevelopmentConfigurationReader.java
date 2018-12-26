/**
 *
 */
package org.arachna.netweaver.dc.config;

import java.io.Reader;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.netweaver.dc.types.PublicPartType;
import org.arachna.xml.DigesterHelper;
import org.arachna.xml.RulesModuleProducer;
import org.xml.sax.Attributes;

/**
 * Reader for a {@link DevelopmentConfiguration} persisted to XML.
 * 
 * @author Dirk Weigenand
 */
public class DevelopmentConfigurationReader implements RulesModuleProducer {

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory developmentComponentFactory;

    /**
     * Create a new <code>DevelopmentConfigurationReader</code> instance. Use the given {@link DevelopmentComponentFactory} to register any
     * {@link DevelopmentComponent}s read in the process of reading the development configuration.
     * 
     * @param developmentComponentFactory
     *            registry for development components
     */
    public DevelopmentConfigurationReader(final DevelopmentComponentFactory developmentComponentFactory) {
        super();
        this.developmentComponentFactory = developmentComponentFactory;
    }

    public DevelopmentConfiguration execute(final Reader reader) {
        return new DigesterHelper<DevelopmentConfiguration>(this).execute(reader);
    }

    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("development-configuration").factoryCreate().usingFactory(new DevelopmentConfigurationFactory()).then()
                    .setProperties();
                forPattern("development-configuration/build-variant").factoryCreate().usingFactory(new BuildVariantFactory()).then()
                    .setNext("setBuildVariant");
                // forPattern("development-configuration/build-variant/option").callMethod("addBuildOption")
                // .withParamTypes(String.class, String.class).then().callParam().ofIndex(0).fromAttribute("name").then().callParam()
                // .ofIndex(1).fromAttribute("value");
                forPattern("development-configuration/compartment").factoryCreate().usingFactory(new CompartmentFactory()).then()
                    .setNext("add");
                forPattern("development-configuration/compartment/used-compartments/used-compartment").factoryCreate()
                    .usingFactory(new CompartmentFactory()).then().setNext("add");
                forPattern("development-configuration/compartment/development-components/development-component").factoryCreate()
                    .usingFactory(new DevelopmentComponentFactory()).then().setNext("add");
                forPattern("development-configuration/compartment/development-components/development-component/description")
                    .callMethod("setDescription").withParamTypes(String.class).usingElementBodyAsArgument();
                forPattern("development-configuration/compartment/development-components/development-component/caption")
                    .callMethod("setCaption").withParamTypes(String.class).usingElementBodyAsArgument();
                forPattern("development-configuration/compartment/development-components/development-component/public-parts/public-part")
                    .factoryCreate().usingFactory(new PublicPartFactory()).then().setNext("add");
                forPattern(
                    "development-configuration/compartment/development-components/development-component/public-parts/public-part/description")
                    .callMethod("setDescription").withParamTypes(String.class).usingElementBodyAsArgument();
                forPattern("development-configuration/compartment/development-components/development-component/dependencies/dependency")
                    .factoryCreate().usingFactory(new PublicPartReferenceFactory()).then().setNext("add");
            }

            class DevelopmentConfigurationFactory extends AbstractObjectCreationFactory<DevelopmentConfiguration> {
                @Override
                public DevelopmentConfiguration createObject(final Attributes attributes) throws Exception {
                    return new DevelopmentConfiguration(attributes.getValue("name"));
                }
            }

            class BuildVariantFactory extends AbstractObjectCreationFactory<BuildVariant> {
                @Override
                public BuildVariant createObject(final Attributes attributes) throws Exception {
                    return new BuildVariant(attributes.getValue("name"), true);
                }
            }

            class CompartmentFactory extends AbstractObjectCreationFactory<Compartment> {
                @Override
                public Compartment createObject(final Attributes attributes) throws Exception {
                    return new Compartment(attributes.getValue("name"), getCompartmentState(attributes.getValue("archive-state")),
                        attributes.getValue("vendor"), attributes.getValue("caption"), attributes.getValue("sc-name"));
                }

                /**
                 * Return the state the compartment is in.
                 * 
                 * @param state
                 *            value of attribute {@link #ARCHIVE_STATE}.
                 * @return the {@link CompartmentState} determined from the attribute value.
                 */
                private CompartmentState getCompartmentState(final String state) {
                    return "yes".equals(state) ? CompartmentState.Archive : CompartmentState.Source;
                }
            }

            class DevelopmentComponentFactory extends AbstractObjectCreationFactory<DevelopmentComponent> {
                @Override
                public DevelopmentComponent createObject(final Attributes attributes) throws Exception {
                    return developmentComponentFactory.create(attributes.getValue("vendor"), attributes.getValue("name"),
                        DevelopmentComponentType.fromString(attributes.getValue("type"), ""));
                }
            }

            class PublicPartFactory extends AbstractObjectCreationFactory<PublicPart> {
                @Override
                public PublicPart createObject(final Attributes attributes) throws Exception {
                    return new PublicPart(attributes.getValue("name"), attributes.getValue("caption"), "",
                        PublicPartType.fromString(attributes.getValue("type")));
                }
            }

            class PublicPartReferenceFactory extends AbstractObjectCreationFactory<PublicPartReference> {
                @Override
                public PublicPartReference createObject(final Attributes attributes) throws Exception {
                    return new PublicPartReference(attributes.getValue("vendor"), attributes.getValue("name"),
                        attributes.getValue("pp-ref"));
                }
            }
        };

    }
}
