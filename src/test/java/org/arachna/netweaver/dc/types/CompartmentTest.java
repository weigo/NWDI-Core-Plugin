/**
 *
 */
package org.arachna.netweaver.dc.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
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
     * example software component descriptor.
     */
    private static final String EXAMPLE_COM_SC1 = "example.com_SC1_1";

    /**
     * example development component name.
     */
    private static final String DEV_CONFIG_NAME = "config";

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
        firstCompartment = Compartment.create(EXAMPLE_COM_SC1, CompartmentState.Source);
        secondCompartment = Compartment.create(EXAMPLE_COM_SC1, CompartmentState.Source);
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
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)} .
     */
    @Test
    public final void testEqualsAllSameAttributesWithNoDevelopmentConfiguration() {
        assertThat(firstCompartment, equalTo(secondCompartment));
    }

    /**
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)} .
     */
    @Test
    public final void testEqualsAllSameAttributesWithDifferentDevelopmentConfigurationObjects() {
        firstCompartment.setDevelopmentConfiguration(new DevelopmentConfiguration(DEV_CONFIG_NAME));
        secondCompartment.setDevelopmentConfiguration(new DevelopmentConfiguration(DEV_CONFIG_NAME));

        assertThat(firstCompartment, equalTo(secondCompartment));
    }

    /**
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)} .
     */
    @Test
    public final void testEqualsAllSameAttributesWithSameDevelopmentConfiguration() {
        final DevelopmentConfiguration developmentConfiguration = new DevelopmentConfiguration(DEV_CONFIG_NAME);

        firstCompartment.setDevelopmentConfiguration(developmentConfiguration);
        secondCompartment.setDevelopmentConfiguration(developmentConfiguration);

        assertThat(firstCompartment, equalTo(secondCompartment));
    }

    /**
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)} .
     */
    @Test
    public final void testEqualsAllSameAttributesWithThisDevelopmentConfigurationIsNull() {
        secondCompartment.setDevelopmentConfiguration(new DevelopmentConfiguration("secondConfig"));

        assertThat(firstCompartment, not(equalTo(secondCompartment)));
        assertThat(secondCompartment, not(equalTo(firstCompartment)));
    }

    /**
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)} .
     */
    @Test
    public final void testEqualsWithDifferentCompartmentNames() {
        secondCompartment = Compartment.create("example.com_SC2_1", CompartmentState.Source);
        assertThat(firstCompartment, not(equalTo(secondCompartment)));
        assertThat(secondCompartment, not(equalTo(firstCompartment)));
    }

    /**
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#create(java.lang.String,CompartmentState)} .
     */
    @Test
    public void assertCreateFromDescriptor() {
        final String compartmentDescriptor = "sap.com_SAP_BUILDT_1";
        final Compartment compartment = Compartment.create(compartmentDescriptor, CompartmentState.Source);

        assertThat(compartment.getName(), equalTo(compartmentDescriptor));
        assertThat(compartment.getVendor(), equalTo("sap.com"));
        assertThat(compartment.getSoftwareComponent(), equalTo("SAP_BUILDT"));
    }
}
