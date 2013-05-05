/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

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
     * SC name for SAP_JTECHS.
     */
    private static final String SAP_JTECHS = "sap.com_SAP_JTECHS_1";

    /**
     * SC name for SAP_BUILDT.
     */
    private static final String SAP_BUILDT = "sap.com_SAP_BUILDT_1";

    /**
     * SC name for SAP_JEE.
     */
    private static final String SAP_JEE = "sap.com_SAP-JEE_1";

    /**
     * example SC name.
     */
    private static final String EXAMPLE_SC1 = "example.com_EXAMPLE_SC1_1";

    /**
     * name of development configuration with a default build variant.
     */
    private static final String CONFDEF_WITH_CONFIGURED_DEFAULT_BUILD_VARIANT =
        "DevelopmentConfigurationWithConfiguredDefaultBuildVariant.confdef";

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
    public void testConfigurationElementRules() {
        final DevelopmentConfiguration config = getConfiguration(CONFDEF_WITH_CONFIGURED_DEFAULT_BUILD_VARIANT);
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
    public void testConfigurationDescriptionRule() {
        final DevelopmentConfiguration config = getConfiguration(CONFDEF_WITH_CONFIGURED_DEFAULT_BUILD_VARIANT);
        assertThat(config, notNullValue());
        assertThat(config.getDescription(), equalTo("Example Track_dev"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public void testConfigurationBuildServerRule() {
        final DevelopmentConfiguration config = getConfiguration(CONFDEF_WITH_CONFIGURED_DEFAULT_BUILD_VARIANT);
        assertThat(config, notNullValue());
        assertThat(config.getBuildServer(), equalTo("http://di0db.example.com:50000"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public void testConfigurationCompartmentRule() {
        final DevelopmentConfiguration config = getConfiguration(CONFDEF_WITH_CONFIGURED_DEFAULT_BUILD_VARIANT);
        assertThat(config, notNullValue());
        final Compartment compartment = config.getCompartment(EXAMPLE_SC1);
        assertThat(compartment, notNullValue());

        final Compartment expectedCompartment = Compartment.create(EXAMPLE_SC1, CompartmentState.Source);
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
    public void testConfigurationCompartmentDtrUrlRule() {
        final DevelopmentConfiguration config = getConfiguration(CONFDEF_WITH_CONFIGURED_DEFAULT_BUILD_VARIANT);
        assertThat(config, notNullValue());
        final Compartment compartment = config.getCompartment(EXAMPLE_SC1);
        assertThat(compartment, notNullValue());
        assertThat(compartment.getDtrUrl(), equalTo("http://di0db.example.com:50000/dtr"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public void testConfigurationCompartmentInactiveLocationRule() {
        final DevelopmentConfiguration config = getConfiguration(CONFDEF_WITH_CONFIGURED_DEFAULT_BUILD_VARIANT);
        assertThat(config, notNullValue());
        final Compartment compartment = config.getCompartment(EXAMPLE_SC1);
        assertThat(compartment, notNullValue());
        assertThat(compartment.getInactiveLocation(), equalTo("ws/Example/example.com_EXAMPLE_SC1/dev/inactive/"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public void testConfigurationCompartmentDependenciesRule() {
        final DevelopmentConfiguration config = getConfiguration(CONFDEF_WITH_CONFIGURED_DEFAULT_BUILD_VARIANT);
        assertThat(config, notNullValue());
        final Compartment compartment = config.getCompartment(EXAMPLE_SC1);
        assertThat(compartment, notNullValue());
        assertThat(compartment.getUsedCompartments(), hasSize(3));

        assertThat(Compartment.create(SAP_JEE, CompartmentState.Archive), isIn(compartment.getUsedCompartments()));
        assertThat(Compartment.create(SAP_BUILDT, CompartmentState.Archive), isIn(compartment.getUsedCompartments()));
        assertThat(Compartment.create(SAP_JTECHS, CompartmentState.Archive), isIn(compartment.getUsedCompartments()));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.confdef.ConfdefReader1#execute(java.io.Reader)}
     * .
     */
    @Test
    public void testConfigurationCompartmentBuildVariantsRule() {
        final DevelopmentConfiguration config = getConfiguration(CONFDEF_WITH_CONFIGURED_DEFAULT_BUILD_VARIANT);
        assertThat(config, notNullValue());
        final Compartment compartment = config.getCompartment(EXAMPLE_SC1);
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

        assertThat(compartments, hasSize(8));

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

    /**
     * Read development configuration from given resource on class path.
     * 
     * @param configFileName
     *            name of config file on class path.
     * @return the development configuration read from the configuration file.
     */
    private DevelopmentConfiguration getConfiguration(final String configFileName) {
        return configurationReader.execute(new InputStreamReader(this.getClass().getResourceAsStream(
            String.format("/org/arachna/netweaver/hudson/nwdi/%s", configFileName))));
    }

    /**
     * Get a collection of expected compartment names.
     * 
     * @return collection of expected compartment names
     */
    private Set<String> getExpectedCompartmentNames() {
        final Set<String> compartmentNames = new HashSet<String>();

        compartmentNames.add(EXAMPLE_SC1);
        compartmentNames.add("sap.com_ENGFACADE_1");
        compartmentNames.add("sap.com_EP_BUILDT_1");
        compartmentNames.add("sap.com_FRAMEWORK_1");
        compartmentNames.add(SAP_BUILDT);
        compartmentNames.add(SAP_JTECHS);
        compartmentNames.add(SAP_JEE);
        compartmentNames.add("sap.com_WD-RUNTIME_1");

        return compartmentNames;
    }
}
