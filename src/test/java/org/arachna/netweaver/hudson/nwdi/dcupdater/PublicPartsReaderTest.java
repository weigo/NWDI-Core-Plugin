/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import hudson.Util;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.arachna.io.UnZip;
import org.arachna.netweaver.dc.types.PublicPart;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 * 
 */
public class PublicPartsReaderTest {

    private File tempDir;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        tempDir = Util.createTempDir();
        final File def = new File(tempDir, "def");
        def.mkdir();
        final URL url = PublicPartsReaderTest.class.getResource("PublicParts.zip");
        url.getFile();

        final UnZip unzipper = new UnZip(def.getAbsolutePath(), url.getPath());
        unzipper.execute();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        Util.deleteContentsRecursive(this.tempDir);
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

}
