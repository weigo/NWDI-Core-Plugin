/**
 *
 */
package org.arachna.netweaver.dc.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link PublicPartByNameComparator}.
 * 
 * @author Dirk Weigenand
 */
public class PublicPartByNameComparatorTest {
    /**
     * the comparator under test.
     */
    private PublicPartByNameComparator comparator;

    /**
     * a public part called 'API'.
     */
    private final PublicPart part1 = new PublicPart("API", null, null, PublicPartType.COMPILE);

    /**
     * a public part called 'ASSEMBLY'.
     */
    private final PublicPart part2 = new PublicPart("ASSEMBLY", null, null, PublicPartType.ASSEMBLY);

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        this.comparator = new PublicPartByNameComparator();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
        this.comparator = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPartByNameComparator#compare(org.arachna.netweaver.dc.types.PublicPart, org.arachna.netweaver.dc.types.PublicPart)}
     * .
     */
    @Test
    public final void testPublicPartsCompareLexicographicallyWithRespectToTheirNamesLhsLtRhs() {
        assertThat(this.comparator.compare(this.part1, this.part2), is(lessThan(0)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPartByNameComparator#compare(org.arachna.netweaver.dc.types.PublicPart, org.arachna.netweaver.dc.types.PublicPart)}
     * .
     */
    @Test
    public final void testPublicPartsCompareLexicographicallyWithRespectToTheirNamesLhsGtRhs() {
        assertThat(this.comparator.compare(this.part2, this.part1), is(greaterThan(0)));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPartByNameComparator#compare(org.arachna.netweaver.dc.types.PublicPart, org.arachna.netweaver.dc.types.PublicPart)}
     * .
     */
    @Test
    public final void testCompareLhsEqualsRhs() {
        assertThat(0, is(equalTo(this.comparator.compare(this.part1, this.part1))));
    }
}
