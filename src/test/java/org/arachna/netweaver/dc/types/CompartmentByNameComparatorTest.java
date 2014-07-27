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
 */
public class CompartmentByNameComparatorTest {
    /**
     * Comparator to use during tests.
     */
    private CompartmentByNameComparator comparator;

    /**
     * An example compartment.
     */
    private final Compartment compartment1 = Compartment.create("example.com_ExampleCompartment_1", CompartmentState.Source);

    /**
     */
    @Before
    public void setUp() {
        comparator = new CompartmentByNameComparator();
    }

    /**
     */
    @After
    public void tearDown() {
        comparator = null;
    }

    /**
     * Test method for
     * {@link CompartmentByNameComparator#compare(org.arachna.netweaver.dc.types.Compartment, org.arachna.netweaver.dc.types.Compartment)}.
     */
    @Test
    public final void testCompareNullArguments() {
        assertThat(comparator.compare(null, null), is(equalTo(0)));
    }

    /**
     * Test method for
     * {@link CompartmentByNameComparator#compare(org.arachna.netweaver.dc.types.Compartment, org.arachna.netweaver.dc.types.Compartment)}.
     */
    @Test
    public final void testCompareOneCompartmentAgainstNull() {
        assertThat(comparator.compare(compartment1, null), is(equalTo(1)));
        assertThat(comparator.compare(null, compartment1), is(equalTo(-1)));
    }

    /**
     * Test method for
     * {@link CompartmentByNameComparator#compare(org.arachna.netweaver.dc.types.Compartment, org.arachna.netweaver.dc.types.Compartment)}.
     */
    @Test
    public final void testCompareOneCompartmentWithSelf() {
        assertThat(comparator.compare(compartment1, compartment1), is(equalTo(0)));
    }

    /**
     * Test method for
     * {@link CompartmentByNameComparator#compare(org.arachna.netweaver.dc.types.Compartment, org.arachna.netweaver.dc.types.Compartment)}.
     */
    @Test
    public final void testCompareTwoCompartments() {
        final Compartment compartment2 = Compartment.create("example.com_ExampleCompartment_2", CompartmentState.Source);
        assertThat(comparator.compare(compartment1, compartment2), is(equalTo(-1)));
        assertThat(comparator.compare(compartment2, compartment1), is(equalTo(1)));
    }
}
