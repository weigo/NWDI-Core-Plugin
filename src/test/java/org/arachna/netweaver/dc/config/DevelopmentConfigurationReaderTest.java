/**
 *
 */
package org.arachna.netweaver.dc.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.IsCollectionContaining.hasItem;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link DevelopmentConfigurationReader}.
 *
 * @author Dirk Weigenand
 */
public class DevelopmentConfigurationReaderTest {
    /**
     *
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     * development configuration unmarshalled from XML.
     */
    private DevelopmentConfiguration configuration;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dcFactory = new DevelopmentComponentFactory();
        configuration = new DevelopmentConfigurationReader(dcFactory).execute(getTestDocument());
    }

    /**
     * @return
     */
    private Reader getTestDocument() {
        final InputStream resource = getClass().getResourceAsStream("/org/arachna/netweaver/dc/config/ExampleDevelopmentConfiguration.xml");
        return new InputStreamReader(resource);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testDevelopmentConfigurationRule() {
        assertThat(configuration, notNullValue(DevelopmentConfiguration.class));
        assertThat(configuration.getCaption(), equalTo("Example_dev"));
        assertThat(configuration.getDescription(), equalTo("Description_dev"));
        assertThat(configuration.getName(), equalTo("DI0_XMPL_D"));
        assertThat(configuration.getVersion(), equalTo("3030"));
    }

    @Test
    public void testBuildVariantRule() {
        assertThat(configuration.getBuildVariant(), notNullValue(BuildVariant.class));
        assertThat(configuration.getBuildVariant().getName(), equalTo("default"));
    }

    @Test
    public void testCompartmentRule() {
        final List<Compartment> compartments = new ArrayList<Compartment>(configuration.getCompartments());
        assertThat(compartments, hasSize(2));
        final Compartment compartment = compartments.get(0);
        assertThat(compartment.getCaption(), equalTo("SAP build components"));
        assertThat(compartment.getName(), equalTo("sap.com_SAP_BUILDT_1"));
        assertThat(compartment.getSoftwareComponent(), equalTo("SAP_BUILDT"));
        assertThat(compartment.getVendor(), equalTo("sap.com"));
        assertThat(compartment.getState(), equalTo(CompartmentState.Archive));
    }

    @Test
    public void testUsingCompartmentRule() {
        final List<Compartment> compartments = new ArrayList<Compartment>(configuration.getCompartments());
        final Compartment compartment = compartments.get(1);
        final List<Compartment> usedCompartments = new ArrayList<Compartment>(compartment.getUsedCompartments());
        final Compartment usedCompartment = usedCompartments.get(0);
        assertThat(usedCompartment.getCaption(), equalTo("Apache POI project libraries"));
        assertThat(usedCompartment.getName(), equalTo("apache.org_APACHE_POI_1"));
        assertThat(usedCompartment.getSoftwareComponent(), equalTo("APACHE_POI"));
        assertThat(usedCompartment.getVendor(), equalTo("apache.org"));
        assertThat(usedCompartment.getState(), equalTo(CompartmentState.Archive));
    }

    @Test
    public void testUsingDevelopmentComponentRule() {
        final List<Compartment> compartments = new ArrayList<Compartment>(configuration.getCompartments());
        final Compartment compartment = compartments.get(0);
        final List<DevelopmentComponent> developmentComponents =
            new ArrayList<DevelopmentComponent>(compartment.getDevelopmentComponents());
        assertThat(developmentComponents, hasSize(2));

        final DevelopmentComponent developmentComponent = developmentComponents.get(0);
        assertThat(developmentComponent.getName(), equalTo("tc/bi/anttasks"));
        assertThat(developmentComponent.getType(), equalTo(DevelopmentComponentType.BuildInfrastructure));
        assertThat(developmentComponent.getVendor(), equalTo("sap.com"));
        assertThat(developmentComponent.getDescription(), equalTo("Ant tasks..."));
    }

    @Test
    public void testPublicPartsRule() {
        final List<Compartment> compartments = new ArrayList<Compartment>(configuration.getCompartments());
        final Compartment compartment = compartments.get(0);
        final List<DevelopmentComponent> developmentComponents =
            new ArrayList<DevelopmentComponent>(compartment.getDevelopmentComponents());
        assertThat(developmentComponents, hasSize(2));

        final DevelopmentComponent developmentComponent = developmentComponents.get(1);
        assertThat(developmentComponent.getName(), equalTo("tc/bi/ant"));
        assertThat(developmentComponent.getType(), equalTo(DevelopmentComponentType.BuildInfrastructure));
        assertThat(developmentComponent.getVendor(), equalTo("sap.com"));
        assertThat(developmentComponent.getDescription(), equalTo("Apache Ant libraries"));
        assertThat(developmentComponent.getCaption(), equalTo("Apache Ant libraries"));

        assertThat(developmentComponent.getPublicParts(), hasItem(new PublicPart("ant", "Build Plugin", "Ant...",
            PublicPartType.INFRASTRUCTURE)));
    }

    @Test
    public void testDependenciesRule() {
        final List<Compartment> compartments = new ArrayList<Compartment>(configuration.getCompartments());
        final Compartment compartment = compartments.get(0);
        final List<DevelopmentComponent> developmentComponents =
            new ArrayList<DevelopmentComponent>(compartment.getDevelopmentComponents());
        assertThat(developmentComponents, hasSize(2));

        final DevelopmentComponent developmentComponent = developmentComponents.get(1);
        assertThat(developmentComponent.getUsedDevelopmentComponents(),
            hasItem(new PublicPartReference("sap.com", "tc/bi/anttasks", "def")));

    }
}
