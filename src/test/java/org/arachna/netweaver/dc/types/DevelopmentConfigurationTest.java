/**
 *
 */
package org.arachna.netweaver.dc.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import org.junit.Test;

/**
 * Unit tests for {@link DevelopmentConfiguration}.
 * 
 * @author Dirk Weigenand
 */
public class DevelopmentConfigurationTest {
    /**
     * A ficticious (and illegal (in the NWDI)) development configuration name
     * without underscores.
     */
    private static final String CONFIG_NAME_WO_UNDER_SCORE = "config";

    /**
     * example development configuration name.
     */
    private static final String PN3_EXAMPLE_CONFIGURATION_NAME = "PN3_ExampleWS_D";

    /**
     * example development configuration name.
     */
    private static final String PN3_EXAMPLE_WS_NAME = "ExampleWS";

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.DevelopmentConfiguration#DevelopmentConfiguration(java.lang.String)}
     * .
     */
    @Test
    public void testCreateAnInstanceOfDevelopmentConfiguration() {
        final DevelopmentConfiguration config = new DevelopmentConfiguration(PN3_EXAMPLE_CONFIGURATION_NAME);
        assertThat(config.getName(), equalTo(PN3_EXAMPLE_CONFIGURATION_NAME));
        assertThat(config.getWorkspace(), equalTo(PN3_EXAMPLE_WS_NAME));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.DevelopmentConfiguration#DevelopmentConfiguration(java.lang.String)}
     * .
     */
    @Test
    public void testCreateAnInstanceOfDevelopmentConfigurationWithoutUnderScoreInName() {
        final DevelopmentConfiguration config = new DevelopmentConfiguration(CONFIG_NAME_WO_UNDER_SCORE);
        assertThat(config.getName(), equalTo(CONFIG_NAME_WO_UNDER_SCORE));
        assertThat(config.getWorkspace(), equalTo(CONFIG_NAME_WO_UNDER_SCORE));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.DevelopmentConfiguration#add(Compartment)}
     * .
     */
    @Test
    public void testAddCompartment() {
        final DevelopmentConfiguration config = new DevelopmentConfiguration("DI1_ExampleTrack_D");

        assertThat(config.getCompartments().size(), equalTo(0));

        final Compartment compartment =
            new Compartment("ExampleCompartment", CompartmentState.Source, "example.com",
                "Caption for ExampleCompartment", "ExampleCompartment_1");
        config.add(compartment);

        assertThat(config.getCompartments().size(), equalTo(1));

        final Compartment compartmentByName = config.getCompartment(compartment.getName());
        assertThat(compartmentByName, equalTo(compartment));
    }

    /**
     * For configurations without build variants defined it should return the
     * highest JdkHomeAlias.
     */
    @Test
    public void assertThatGetJdkHomeAliasReturnsLatestDefinedJdkHomeAliasWithoutConfiguredBuildVariant() {
        final DevelopmentConfiguration config = new DevelopmentConfiguration("DI1_ExampleTrack_D");

        final JdkHomeAlias alias = config.getJdkHomeAlias();
        assertThat(alias, notNullValue());
        final JdkHomeAlias[] aliases = JdkHomeAlias.values();
        assertThat(alias, equalTo(aliases[aliases.length - 1]));
    }

    /**
     * For configurations without build variants defined it should return the
     * highest JdkHomeAlias.
     */
    @Test
    public void assertThatGetJdkHomeAliasReturnsLatestDefinedJdkHomeAliasWithBuildVariantButNoJdkHomePath() {
        final DevelopmentConfiguration config = new DevelopmentConfiguration("DI1_ExampleTrack_D");
        config.setBuildVariant(new BuildVariant("default", true));

        final JdkHomeAlias alias = config.getJdkHomeAlias();
        assertThat(alias, notNullValue());
        final JdkHomeAlias[] aliases = JdkHomeAlias.values();
        assertThat(alias, equalTo(aliases[aliases.length - 1]));
    }

    /**
     * For configurations with a build variant defined it should return the
     * configured JdkHomeAlias.
     */
    @Test
    public void assertThatGetJdkHomeAliasReturnsAliasDefinedViaJdkHomePathKey() {
        final DevelopmentConfiguration config = new DevelopmentConfiguration("DI1_ExampleTrack_D");
        final BuildVariant buildVariant = new BuildVariant("default", true);
        buildVariant.addBuildOption(BuildVariant.COM_SAP_JDK_HOME_PATH_KEY, JdkHomeAlias.Jdk142Home.toString());
        config.setBuildVariant(buildVariant);
        assertThat(config.getJdkHomeAlias(), equalTo(JdkHomeAlias.Jdk142Home));
    }
}
