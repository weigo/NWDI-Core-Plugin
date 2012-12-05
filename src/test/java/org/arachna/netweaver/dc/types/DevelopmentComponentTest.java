/**
 *
 */
package org.arachna.netweaver.dc.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for development components.
 * 
 * @author Dirk Weigenand
 */
public class DevelopmentComponentTest {
    /**
     * the development component used throughout the test.
     */
    private DevelopmentComponent component;

    /**
     * Set up fixture.
     */
    @Before
    public void setUp() {
        component = new DevelopmentComponent("", "");
    }

    /**
     * Test method for {@link java.lang.Object#equals(java.lang.Object)}.
     */
    @Test
    public final void testEqualsThisObject() {
        assertTrue(component.equals(component));
    }

    /**
     * Test method for {@link java.lang.Object#equals(java.lang.Object)}.
     */
    @Test
    public final void testNotEqualsNullObject() {
        // @CHECKSTYLE:OFF
        assertThat(false, equalTo(component.equals(null)));
        // @CHECKSTYLE:ON
    }

    /**
     * Assert that public parts are returned sorted lexicographically when
     * returned by {@link DevelopmentComponent#getPublicParts()}.
     */
    @Test
    public void testGetPublicPartsReturnedAreSorted() {
        final PublicPart part1 = new PublicPart("API", null, null, PublicPartType.COMPILE);
        final PublicPart part2 = new PublicPart("ASSEMBLY", null, null, PublicPartType.COMPILE);
        component.add(part1);
        component.add(part2);
        final Iterator<PublicPart> publicParts = component.getPublicParts().iterator();
        assertThat(part1, is(equalTo(publicParts.next())));
        assertThat(part2, is(equalTo(publicParts.next())));
    }

    /**
     * Assert that adding a public part returns it indeed.
     */
    @Test
    public void testGetPublicPartsReturnedAreNotEmpty() {
        final PublicPart part1 = new PublicPart("API", null, null, PublicPartType.COMPILE);
        component.add(part1);
        assertThat(1, is(equalTo(component.getPublicParts().size())));
    }

    @Test
    public void testNormalize() {
        component = new DevelopmentComponent("name/subname", "vendor");
        assertThat(component.getNormalizedName(" "), equalTo("vendor name subname"));
    }
}
