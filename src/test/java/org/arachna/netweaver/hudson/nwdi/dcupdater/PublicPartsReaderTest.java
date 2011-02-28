/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.util.List;

import org.arachna.netweaver.dc.types.PublicPart;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link PublicPartsReader}.
 * 
 * @author Dirk Weigenand
 */
public class PublicPartsReaderTest extends AbstractZipFileContentProvidingTest {

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.dcupdater.PublicPartsReader#read()}
     * .
     */
    @Test
    public final void testRead() {
        final PublicPartsReader reader = new PublicPartsReader(this.tempDir.getAbsolutePath());
        final List<PublicPart> publicParts = reader.read();

        assertThat(publicParts, hasSize(2));
    }

    @Override
    protected String getResourceName() {
        return "PublicParts.zip";
    }
}
