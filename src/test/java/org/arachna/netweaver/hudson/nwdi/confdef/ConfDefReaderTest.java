/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.confdef;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIn.isIn;

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.arachna.netweaver.dc.types.BuildOption;
import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit-Test for reading '.confdef' configuration files using
 * {@link ConfDefReader}.
 * 
 * @author Dirk Weigenand
 */
public final class ConfDefReaderTest {
    /**
     * instance under test.
     */
    private ConfDefReader configurationReader;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        configurationReader = new ConfDefReader();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
        configurationReader = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testConfigurationElementRules() {
        final DevelopmentConfiguration config =
            getConfiguration("DevelopmentConfigurationWithConfiguredDefaultBuildVariant.confdef");
        assertThat(config, notNullValue());
        assertThat(config.getName(), equalTo("DI0_Example_D"));
        assertThat(config.getWorkspace(), equalTo("Example"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testConfigurationDescriptionRule() {
        final DevelopmentConfiguration config =
            getConfiguration("DevelopmentConfigurationWithConfiguredDefaultBuildVariant.confdef");
        assertThat(config, notNullValue());
        assertThat(config.getDescription(), equalTo("Example Track_dev"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testConfigurationBuildServerRule() {
        final DevelopmentConfiguration config =
            getConfiguration("DevelopmentConfigurationWithConfiguredDefaultBuildVariant.confdef");
        assertThat(config, notNullValue());
        assertThat(config.getBuildServer(), equalTo("http://di0db.example.com:50000"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testConfigurationCompartmentRule() {
        final DevelopmentConfiguration config =
            getConfiguration("DevelopmentConfigurationWithConfiguredDefaultBuildVariant.confdef");
        assertThat(config, notNullValue());
        final String compartmentName = "example.com_EXAMPLE_SC1_1";
        final Compartment compartment = config.getCompartment(compartmentName);
        assertThat(compartment, notNullValue());

        final Compartment expectedCompartment = Compartment.create(compartmentName, CompartmentState.Source);
        assertThat(compartment.getName(), equalTo(expectedCompartment.getName()));
        assertThat(compartment.getCaption(), equalTo(expectedCompartment.getCaption()));
        assertThat(compartment.getSoftwareComponent(), equalTo(expectedCompartment.getSoftwareComponent()));
        assertThat(compartment.getState(), equalTo(expectedCompartment.getState()));
        assertThat(compartment.getVendor(), equalTo(expectedCompartment.getVendor()));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testConfigurationCompartmentDtrUrlRule() {
        final DevelopmentConfiguration config =
            getConfiguration("DevelopmentConfigurationWithConfiguredDefaultBuildVariant.confdef");
        assertThat(config, notNullValue());
        final Compartment compartment = config.getCompartment("example.com_EXAMPLE_SC1_1");
        assertThat(compartment, notNullValue());
        assertThat(compartment.getDtrUrl(), equalTo("http://di0db.example.com:50000/dtr"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testConfigurationCompartmentInactiveLocationRule() {
        final DevelopmentConfiguration config =
            getConfiguration("DevelopmentConfigurationWithConfiguredDefaultBuildVariant.confdef");
        assertThat(config, notNullValue());
        final Compartment compartment = config.getCompartment("example.com_EXAMPLE_SC1_1");
        assertThat(compartment, notNullValue());
        assertThat(compartment.getInactiveLocation(), equalTo("ws/Example/example.com_EXAMPLE_SC1/dev/inactive/"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testConfigurationCompartmentDependenciesRule() {
        final DevelopmentConfiguration config =
            getConfiguration("DevelopmentConfigurationWithConfiguredDefaultBuildVariant.confdef");
        assertThat(config, notNullValue());
        final Compartment compartment = config.getCompartment("example.com_EXAMPLE_SC1_1");
        assertThat(compartment, notNullValue());
        assertThat(compartment.getUsedCompartments(), hasSize(3));

        assertThat(Compartment.create("sap.com_SAP-JEE_1", CompartmentState.Archive),
            isIn(compartment.getUsedCompartments()));
        assertThat(Compartment.create("sap.com_SAP_BUILDT_1", CompartmentState.Archive),
            isIn(compartment.getUsedCompartments()));
        assertThat(Compartment.create("sap.com_SAP_JTECHS_1", CompartmentState.Archive),
            isIn(compartment.getUsedCompartments()));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testConfigurationCompartmentBuildVariantsRule() {
        final DevelopmentConfiguration config =
            getConfiguration("DevelopmentConfigurationWithConfiguredDefaultBuildVariant.confdef");
        assertThat(config, notNullValue());
        final Compartment compartment = config.getCompartment("example.com_EXAMPLE_SC1_1");
        assertThat(compartment, notNullValue());
        final BuildVariant expectedBuildVariant = new BuildVariant("default", true);
        expectedBuildVariant.add(new BuildOption("com.sap.jdk.home_path_key", "JDK1.3.1_HOME"));
        expectedBuildVariant.add(new BuildOption("com.sap.jdk.javac.force_fork", "true"));
        assertThat(compartment.getBuildVariants(), hasItem(expectedBuildVariant));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public void testReadExampleConfDef() {
        final DevelopmentConfiguration configuration = getConfiguration("Example.confdef");

        final Collection<Compartment> compartments = configuration.getCompartments();

        assertThat(8, equalTo(compartments.size()));

        final Set<String> compartmentNames = getExpectedCompartmentNames();

        for (final Compartment compartment : compartments) {
            assertThat(compartmentNames, hasItem(compartment.getName()));
        }

    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public void testReadExampleConfDefAndVerifyThatGetCompartmentsReturnsTheCorrectCompartments() {
        final DevelopmentConfiguration configuration = getConfiguration("Example.confdef");

        for (final String compartmentName : getExpectedCompartmentNames()) {
            assertThat(configuration.getCompartment(compartmentName), notNullValue());
        }
    }

    private DevelopmentConfiguration getConfiguration(final String configFileName) {
        return configurationReader.execute(new InputStreamReader(this.getClass().getResourceAsStream(
            String.format("/org/arachna/netweaver/hudson/nwdi/confdef/%s", configFileName))));
    }

    /**
     * @return
     */
    private Set<String> getExpectedCompartmentNames() {
        final Set<String> compartmentNames = new HashSet<String>();

        compartmentNames.add("example.com_EXAMPLE_SC1_1");
        compartmentNames.add("sap.com_FRAMEWORK_1");
        compartmentNames.add("sap.com_ENGFACADE_1");
        compartmentNames.add("sap.com_EP_BUILDT_1");
        compartmentNames.add("sap.com_FRAMEWORK_1");
        compartmentNames.add("sap.com_SAP_BUILDT_1");
        compartmentNames.add("sap.com_SAP_JTECHS_1");
        compartmentNames.add("sap.com_SAP-JEE_1");
        compartmentNames.add("sap.com_WD-RUNTIME_1");

        return compartmentNames;
    }
}
