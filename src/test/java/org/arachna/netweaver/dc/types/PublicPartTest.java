/**
 * 
 */
package org.arachna.netweaver.dc.types;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * JUnit test for {@link PublicPart}.
 * 
 * @author Dirk Weigenand
 * 
 */
public class PublicPartTest {
    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPart#PublicPart(java.lang.String, java.lang.String, java.lang.String, org.arachna.netweaver.dc.types.PublicPartType)}
     * .
     */
    @Test(expected = IllegalArgumentException.class)
    public final void testThatNewPublicPartWithNullPublicPartTypeThrowsIllegalArgumentException() {
        new PublicPart(null, null, null, null);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPart#PublicPart(java.lang.String, java.lang.String, java.lang.String, org.arachna.netweaver.dc.types.PublicPartType)}
     * .
     */
    @Test
    public final void testThatNewPublicPartWithNullDescriptionInitializesDescriptionToEmptyString() {
        assertThat(new PublicPart("", null, null, PublicPartType.COMPILE).getDescription(), equalTo(""));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.dc.types.PublicPart#PublicPart(java.lang.String, java.lang.String, java.lang.String, org.arachna.netweaver.dc.types.PublicPartType)}
     * .
     */
    @Test
    public final void testThatNewPublicPartWithNullCaptionInitializesCaptionToEmptyString() {
        assertThat(new PublicPart("", null, null, PublicPartType.COMPILE).getCaption(), equalTo(""));
    }
}
