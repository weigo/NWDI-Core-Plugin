/**
 *
 */
package org.arachna.netweaver.dc.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

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
        assertThat(config.getName(), is(equalTo(PN3_EXAMPLE_CONFIGURATION_NAME)));
        assertThat(config.getWorkspace(), is(equalTo(PN3_EXAMPLE_WS_NAME)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.DevelopmentConfiguration#DevelopmentConfiguration(java.lang.String)}
     * .
     */
    @Test
    public void testCreateAnInstanceOfDevelopmentConfigurationWithoutUnderScoreInName() {
        final DevelopmentConfiguration config = new DevelopmentConfiguration(CONFIG_NAME_WO_UNDER_SCORE);
        assertThat(config.getName(), is(equalTo(CONFIG_NAME_WO_UNDER_SCORE)));
        assertThat(config.getWorkspace(), is(equalTo(CONFIG_NAME_WO_UNDER_SCORE)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.DevelopmentConfiguration#add(Compartment)}
     * .
     */
    @Test
    public void testAddCompartment() {
        final DevelopmentConfiguration config = new DevelopmentConfiguration("DI1_ExampleTrack_D");

        assertThat(config.getCompartments().size(), is(equalTo(0)));

        final Compartment compartment =
            new Compartment("ExampleCompartment", CompartmentState.Source, "example.com", "Caption for ExampleCompartment",
                "ExampleCompartment_1");
        config.add(compartment);

        assertThat(config.getCompartments().size(), is(equalTo(1)));

        final Compartment compartmentByName = config.getCompartment(compartment.getName());
        assertThat(compartmentByName, is(equalTo(compartment)));
    }
}
