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
 * Unit tests for {@link PublicPartReferenceComparator}.
 * 
 * @author Dirk Weigenand
 */
public class PublicPartReferenceComparatorTest {
    /**
     * name of public part 'B'.
     */
    private static final String PUBLIC_PART_B = "B";

    /**
     * name of public part 'A'.
     */
    private static final String PUBLIC_PART_A = "A";

    /**
     * name of development component 'DC B'.
     */
    private static final String DC_B = "DC B";

    /**
     * name of development component 'DC A'.
     */
    private static final String DC_A = "DC A";

    /**
     * vendor 'example.com'.
     */
    private static final String EXAMPLE_COM = "example.com";

    /**
     * the comparator to test.
     */
    private PublicPartReferenceComparator comparator;

    /**
     * Set up fixture: comparator to test.
     */
    @Before
    public void setUp() {
        this.comparator = new PublicPartReferenceComparator();
    }

    /**
     */
    @After
    public void tearDown() {
        this.comparator = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPartReferenceComparator#compare(org.arachna.netweaver.dc.types.PublicPartReference, org.arachna.netweaver.dc.types.PublicPartReference)}
     * .
     */
    @Test
    public final void testCompareWithSelf() {
        final PublicPartReference reference = new PublicPartReference(EXAMPLE_COM, DC_A, "");
        assertThat(this.comparator.compare(reference, reference), is(equalTo(0)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPartReferenceComparator#compare(org.arachna.netweaver.dc.types.PublicPartReference, org.arachna.netweaver.dc.types.PublicPartReference)}
     * .
     */
    @Test
    public final void testThatCompareWithSameVendorSortsAndEmptyPublicPartNameLexicographicallyByDCName() {
        final PublicPartReference dcRefToA = new PublicPartReference(EXAMPLE_COM, DC_A, "");
        final PublicPartReference dcRefToB = new PublicPartReference(EXAMPLE_COM, DC_B, "");
        assertThat(this.comparator.compare(dcRefToA, dcRefToB), is(equalTo(-1)));
        assertThat(this.comparator.compare(dcRefToB, dcRefToA), is(equalTo(1)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPartReferenceComparator#compare(org.arachna.netweaver.dc.types.PublicPartReference, org.arachna.netweaver.dc.types.PublicPartReference)}
     * .
     */
    @Test
    public final void testThatCompareWithSameVendorAndSameDCNameSortsLexicographicallyByPublicPartName() {
        final PublicPartReference dcRefToA = new PublicPartReference(EXAMPLE_COM, DC_A, PUBLIC_PART_A);
        final PublicPartReference dcRefToB = new PublicPartReference(EXAMPLE_COM, DC_A, PUBLIC_PART_B);
        assertThat(this.comparator.compare(dcRefToA, dcRefToB), is(equalTo(-1)));
        assertThat(this.comparator.compare(dcRefToB, dcRefToA), is(equalTo(1)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPartReferenceComparator#compare(org.arachna.netweaver.dc.types.PublicPartReference, org.arachna.netweaver.dc.types.PublicPartReference)}
     * .
     */
    @Test
    public final void testThatCompareWithSameVendorAndDifferentDCNameSortsLexicographicallyByDCNameAndThenPublicPartName() {
        final PublicPartReference dcRefToA = new PublicPartReference(EXAMPLE_COM, DC_A, PUBLIC_PART_A);
        final PublicPartReference dcRefToB = new PublicPartReference(EXAMPLE_COM, DC_B, PUBLIC_PART_A);
        assertThat(this.comparator.compare(dcRefToA, dcRefToB), is(equalTo(-1)));
        assertThat(this.comparator.compare(dcRefToB, dcRefToA), is(equalTo(1)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPartReferenceComparator#compare(org.arachna.netweaver.dc.types.PublicPartReference, org.arachna.netweaver.dc.types.PublicPartReference)}
     * .
     */
    @Test
    public final void testThatCompareWithDifferentVendorAndEqualDCNameSortsLexicographicallyByVendorAndThenDCNameAndThenPublicPartName() {
        final PublicPartReference dcRefToA = new PublicPartReference(EXAMPLE_COM, DC_A, PUBLIC_PART_A);
        final PublicPartReference dcRefToB = new PublicPartReference("example.org", DC_A, PUBLIC_PART_A);
        assertThat(this.comparator.compare(dcRefToA, dcRefToB), is(equalTo(-1)));
        assertThat(this.comparator.compare(dcRefToB, dcRefToA), is(equalTo(1)));
    }
}
