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
        this.firstCompartment = new Compartment("Compartment", CompartmentState.Source, "example.com", "description", "Softwarecomponent");
        this.secondCompartment = new Compartment("Compartment", CompartmentState.Source, "example.com", "description", "Softwarecomponent");
    }

    /**
     * Tear down fixture.
     */
    @After
    public void tearDown() {
        this.firstCompartment = null;
        this.secondCompartment = null;
    }

    /**
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)} .
     */
    @Test
    public final void testEqualsAllSameAttributesWithNoDevelopmentConfiguration() {
        assertThat(firstCompartment, is(equalTo(secondCompartment)));
    }

    /**
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)} .
     */
    @Test
    public final void testEqualsAllSameAttributesWithDifferentDevelopmentConfigurationObjects() {
        this.firstCompartment.setDevelopmentConfiguration(new DevelopmentConfiguration("config"));
        this.secondCompartment.setDevelopmentConfiguration(new DevelopmentConfiguration("config"));

        assertThat(this.firstCompartment, is(equalTo(this.secondCompartment)));
    }

    /**
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)} .
     */
    @Test
    public final void testEqualsAllSameAttributesWithSameDevelopmentConfiguration() {
        final DevelopmentConfiguration developmentConfiguration = new DevelopmentConfiguration("config");

        this.firstCompartment.setDevelopmentConfiguration(developmentConfiguration);
        this.secondCompartment.setDevelopmentConfiguration(developmentConfiguration);

        assertThat(this.firstCompartment, is(equalTo(this.secondCompartment)));
    }

    /**
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)} .
     */
    @Test
    public final void testEqualsAllSameAttributesWithThisDevelopmentConfigurationIsNull() {
        this.secondCompartment.setDevelopmentConfiguration(new DevelopmentConfiguration("secondConfig"));

        assertThat(this.firstCompartment, is(not(equalTo(this.secondCompartment))));
        assertThat(this.secondCompartment, is(not(equalTo(this.firstCompartment))));
    }

    /**
     * Test method for {@link org.arachna.netweaver.dc.types.Compartment#equals(java.lang.Object)} .
     */
    @Test
    public final void testEqualsWithDifferentCompartmentNames() {
        this.firstCompartment =
                new Compartment("First Compartment", CompartmentState.Source, "example.com", "description", "Softwarecomponent");
        this.secondCompartment =
                new Compartment("Second Compartment", CompartmentState.Source, "example.com", "description", "Softwarecomponent");
        assertThat(this.firstCompartment, is(not(equalTo(this.secondCompartment))));
        assertThat(this.secondCompartment, is(not(equalTo(this.firstCompartment))));
    }
}
