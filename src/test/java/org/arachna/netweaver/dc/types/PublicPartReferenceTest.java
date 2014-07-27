/**
 * 
 */
package org.arachna.netweaver.dc.types;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * JUnit tests for {@link PublicPartReference}.
 * 
 * @author Dirk Weigenand
 */
public class PublicPartReferenceTest {
    /**
     * name of referenced DC.
     */
    private static final String NAME = "name";

    /**
     * vendor name.
     */
    private static final String VENDOR = "vendor";

    /**
     * Assert two references are considered equal when they share vendor and
     * component name (with an empty public part name).
     */
    @Test
    public final void testEqualsOthersReferenceWithSameProperties() {
        assertThat(new PublicPartReference(VENDOR, NAME), equalTo(new PublicPartReference(VENDOR, NAME)));
    }

    /**
     * Assert two references are considered equal when they share vendor and
     * component name (with an empty public part name, explicitly in the second
     * reference).
     */
    @Test
    public final void testEqualsOthersReferenceWithSameProperties1() {
        assertThat(new PublicPartReference(VENDOR, NAME), equalTo(new PublicPartReference(VENDOR, NAME, "")));
    }

    /**
     * Assert two references are considered equal when they share vendor and
     * component name (with an empty public part name, explicitly in the second
     * reference).
     */
    @Test
    public final void testEqualsOthersReferenceWithSameProperties2() {
        assertThat(new PublicPartReference(VENDOR, NAME),
            not(equalTo(new PublicPartReference(VENDOR, NAME, "assembly"))));
    }
}
