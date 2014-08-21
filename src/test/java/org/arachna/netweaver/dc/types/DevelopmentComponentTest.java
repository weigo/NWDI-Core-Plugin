/**
 *
 */
package org.arachna.netweaver.dc.types;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for development components.
 *
 * @author Dirk Weigenand
 */
public class DevelopmentComponentTest {
    /**
     * public part name 'API'.
     */
    private static final String API = "API";

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
     * Assert that adding a public part returns it indeed.
     */
    @Test
    public void testGetPublicPartsReturnedAreNotEmpty() {
        final PublicPart part1 = new PublicPart(API, null, null, PublicPartType.COMPILE);
        component.add(part1);
        assertThat(1, is(equalTo(component.getPublicParts().size())));
    }

    /**
     * Verify {@link DevelopmentComponent#getNormalizedName()}.
     */
    @Test
    public void testNormalize() {
        component = new DevelopmentComponent("name/subname", "vendor");
        assertThat(component.getNormalizedName(" "), equalTo("vendor name subname"));
    }
}
