/**
 *
 */
package org.arachna.netweaver.dc.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link Compartment}.
 * 
 * @author Dirk Weigenand
 */
public class CompartmentTest {
    /**
     * First compartment for equals tests.
     */
    private Compartment firstCompartment;

    /**
     * Second compartment for equals tests.
     */
    private Compartment secondCompartment;

    /**
     * Initialize fixture.
     */
    @Before
    public void setUp() {
        firstCompartment =
            new Compartment("Compartment", CompartmentState.Source, "example.com", "description", "Softwarecomponent");
        secondCompartment =
            new Compartment("Compartment", CompartmentState.Source, "example.com", "description", "Softwarecomponent");
    }

    /**
     * Tear down fixture.
     */
    @After
    public void tearDown() {
        firstCompartment = null;
        secondCompartment = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)}
     * .
     */
    @Test
    public final void testEqualsAllSameAttributesWithNoDevelopmentConfiguration() {
        assertThat(firstCompartment, is(equalTo(secondCompartment)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)}
     * .
     */
    @Test
    public final void testEqualsAllSameAttributesWithDifferentDevelopmentConfigurationObjects() {
        firstCompartment.setDevelopmentConfiguration(new DevelopmentConfiguration("config"));
        secondCompartment.setDevelopmentConfiguration(new DevelopmentConfiguration("config"));

        assertThat(firstCompartment, is(equalTo(secondCompartment)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)}
     * .
     */
    @Test
    public final void testEqualsAllSameAttributesWithSameDevelopmentConfiguration() {
        final DevelopmentConfiguration developmentConfiguration = new DevelopmentConfiguration("config");

        firstCompartment.setDevelopmentConfiguration(developmentConfiguration);
        secondCompartment.setDevelopmentConfiguration(developmentConfiguration);

        assertThat(firstCompartment, is(equalTo(secondCompartment)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)}
     * .
     */
    @Test
    public final void testEqualsAllSameAttributesWithThisDevelopmentConfigurationIsNull() {
        secondCompartment.setDevelopmentConfiguration(new DevelopmentConfiguration("secondConfig"));

        assertThat(firstCompartment, is(not(equalTo(secondCompartment))));
        assertThat(secondCompartment, is(not(equalTo(firstCompartment))));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)}
     * .
     */
    @Test
    public final void testEqualsWithDifferentCompartmentNames() {
        firstCompartment =
            new Compartment("First Compartment", CompartmentState.Source, "example.com", "description",
                "Softwarecomponent");
        secondCompartment =
            new Compartment("Second Compartment", CompartmentState.Source, "example.com", "description",
                "Softwarecomponent");
        assertThat(firstCompartment, is(not(equalTo(secondCompartment))));
        assertThat(secondCompartment, is(not(equalTo(firstCompartment))));
    }

    @Test
    public void assertCreateFromDescriptor() {
        final String compartmentDescriptor = "sap.com_SAP_BUILDT_1";
        final Compartment compartment = Compartment.create(compartmentDescriptor, CompartmentState.Source);

        assertThat(compartment.getName(), equalTo(compartmentDescriptor));
        assertThat(compartment.getVendor(), equalTo("sap.com"));
        assertThat(compartment.getSoftwareComponent(), equalTo("SAP_BUILDT"));
    }
}
