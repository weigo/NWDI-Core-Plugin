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
        this.component = new DevelopmentComponent("", "");
    }

    /**
     * Test method for {@link java.lang.Object#equals(java.lang.Object)}.
     */
    @Test
    public final void testEqualsThisObject() {
        assertTrue(this.component.equals(component));
    }

    /**
     * Test method for {@link java.lang.Object#equals(java.lang.Object)}.
     */
    @Test
    public final void testNotEqualsNullObject() {
        assertThat(false, is(equalTo(this.component.equals(null))));
    }

    /**
     * Assert that public parts are returned sorted lexicographically when
     * returned by {@link DevelopmentComponent#getPublicParts()}.
     */
    @Test
    public void testGetPublicPartsReturnedAreSorted() {
        final PublicPart part1 = new PublicPart("API", null, null);
        final PublicPart part2 = new PublicPart("ASSEMBLY", null, null);
        this.component.add(part1);
        this.component.add(part2);
        final Iterator<PublicPart> publicParts = this.component.getPublicParts().iterator();
        assertThat(part1, is(equalTo(publicParts.next())));
        assertThat(part2, is(equalTo(publicParts.next())));
    }

    /**
     * Assert that adding a public part returns it indeed.
     */
    @Test
    public void testGetPublicPartsReturnedAreNotEmpty() {
        final PublicPart part1 = new PublicPart("API", null, null);
        this.component.add(part1);
        assertThat(1, is(equalTo(this.component.getPublicParts().size())));
    }
}
