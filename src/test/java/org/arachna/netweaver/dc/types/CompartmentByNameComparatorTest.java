/**
 *
 */
package org.arachna.netweaver.dc.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link CompartmentByNameComparator}.
 * 
 * @author Dirk Weigenand
 * 
 */
public class CompartmentByNameComparatorTest {
    /**
     * Comparator to use during tests.
     */
    private CompartmentByNameComparator comparator;

    /**
     */
    @Before
    public void setUp() {
        this.comparator = new CompartmentByNameComparator();
    }

    /**
     */
    @After
    public void tearDown() {
        this.comparator = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.CompartmentByNameComparator#compare(org.arachna.netweaver.dc.types.Compartment, org.arachna.netweaver.dc.types.Compartment)}
     * .
     */
    @Test
    public final void testCompareNullArguments() {
        assertThat(this.comparator.compare(null, null), is(equalTo(0)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.CompartmentByNameComparator#compare(org.arachna.netweaver.dc.types.Compartment, org.arachna.netweaver.dc.types.Compartment)}
     * .
     */
    @Test
    public final void testCompareOneCompartmentAgainstNull() {
        final Compartment compartment =
            new Compartment("ExampleCompartment", CompartmentState.Source, "example.com", "", "ExampleCompartment_1");
        assertThat(this.comparator.compare(compartment, null), is(equalTo(1)));
        assertThat(this.comparator.compare(null, compartment), is(equalTo(-1)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.CompartmentByNameComparator#compare(org.arachna.netweaver.dc.types.Compartment, org.arachna.netweaver.dc.types.Compartment)}
     * .
     */
    @Test
    public final void testCompareOneCompartmentWithSelf() {
        final Compartment compartment =
            new Compartment("ExampleCompartment", CompartmentState.Source, "example.com", "", "ExampleCompartment_1");
        assertThat(this.comparator.compare(compartment, compartment), is(equalTo(0)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.CompartmentByNameComparator#compare(org.arachna.netweaver.dc.types.Compartment, org.arachna.netweaver.dc.types.Compartment)}
     * .
     */
    @Test
    public final void testCompareTwoCompartments() {
        final Compartment compartment1 =
            new Compartment("ExampleCompartment", CompartmentState.Source, "example.com", "", "ExampleCompartment_1");
        final Compartment compartment2 =
            new Compartment("ExampleCompartment1", CompartmentState.Source, "example.com", "", "ExampleCompartment1_1");
        assertThat(this.comparator.compare(compartment1, compartment2), is(equalTo(-1)));
        assertThat(this.comparator.compare(compartment2, compartment1), is(equalTo(1)));
    }
}
