/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import hudson.Util;

import java.io.File;
import java.net.URL;

import org.arachna.io.UnZip;
import org.junit.After;
import org.junit.Before;

/**
 * @author Dirk Weigenand
 */
public abstract class AbstractZipFileContentProvidingTest {
    /**
     * temporary directory for storing public parts.
     */
    protected File tempDir;

    protected abstract String getResourceName();

    protected final String getBasePath() {
        return this.tempDir.getAbsolutePath();
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.tempDir = Util.createTempDir();
        final URL url = getClass().getResource(this.getResourceName());
        new UnZip(getBasePath(), url.getPath()).execute();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        Util.deleteContentsRecursive(this.tempDir);
    }

    /**
     *
     */
    public AbstractZipFileContentProvidingTest() {
        super();
    }
}
