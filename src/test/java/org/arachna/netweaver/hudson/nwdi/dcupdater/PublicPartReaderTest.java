/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertNotNull;

import java.io.InputStreamReader;

import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartType;
import org.arachna.xml.DigesterHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link PublicPartRulesModuleProducer}.
 * 
 * @author Dirk Weigenand
 */
public class PublicPartReaderTest {
    /**
     * PublicPart read from example file.
     */
    private PublicPart publicPart;

    /**
     * Set up fixture.
     */
    @Before
    public void setUp() {
        final DigesterHelper<PublicPart> digesterHelper =
            new DigesterHelper<PublicPart>(new PublicPartRulesModuleProducer());
        publicPart =
            digesterHelper.execute(new InputStreamReader(this.getClass().getResourceAsStream(
                "/org/arachna/netweaver/hudson/nwdi/dcupdater/API.pp")));
    }

    /**
     * Tear down fixture.
     */
    @After
    public void tearDown() {
        publicPart = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.dcupdater.PublicPartRulesModuleProducer}
     * .
     */
    @Test
    public final void testPublicPartName() {
        assertNotNull(publicPart);
        assertThat(publicPart.getPublicPart(), equalTo("API"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.dcupdater.PublicPartRulesModuleProducer}
     * .
     */
    @Test
    public final void testPublicPartCaption() {
        assertNotNull(publicPart);
        assertThat(publicPart.getCaption(), equalTo("caption"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.dcupdater.PublicPartRulesModuleProducer}
     * .
     */
    @Test
    public final void testPublicPartDescription() {
        assertNotNull(publicPart);
        assertThat(publicPart.getDescription(), equalTo("description"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.dcupdater.PublicPartRulesModuleProducer}
     * .
     */
    @Test
    public final void testPublicPartType() {
        assertNotNull(publicPart);
        assertThat(publicPart.getType(), equalTo(PublicPartType.COMPILE));
    }
}
