/**
 *
 */
package org.arachna.netweaver.tools.dc;

import static org.junit.Assert.fail;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.util.StreamTaskListener;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.JdkHomeAlias;
import org.arachna.netweaver.dc.types.JdkHomePaths;
import org.arachna.netweaver.tools.DIToolCommandBuilder;
import org.arachna.netweaver.tools.DIToolDescriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit tests for {@link DCToolCommandExecutor}.
 * 
 * @author Dirk Weigenand
 */
public class DCToolCommandExecutorTest {
    /**
     * default encoding.
     */
    private static final String UTF_8 = "UTF-8";

    /**
     * use base 8 for creating file system permissions.
     */
    private static final int OCTAL = 8;

    /**
     * The folder used for testing.
     */
    private File testDirectory;

    /**
     * Folder where the NWDI tool libraries are found.
     */
    private String nwdiToolLibDir;

    /**
     * the {@link DCToolCommandExecutor} instance under test.
     */
    private DCToolCommandExecutor executor;

    /**
     * Create a temporary directory for the test and set up the objects used for
     * this test.
     * 
     * @throws IOException
     *             when the temporary directory for the test could not be
     *             created or creating the DCToolCommandExecutor fails.
     * @throws InterruptedException
     *             when the test was interrupted during file operations on the
     *             test scripts
     * 
     * @throws IOException
     *             rethrown when creating the DCToolCommandExecutor fails
     * @throws InterruptedException
     */
    @Before
    public void setUp() throws IOException, InterruptedException {
        testDirectory = Util.createTempDir();

        if (testDirectory == null || !testDirectory.exists()) {
            fail("Could not create " + testDirectory.getAbsolutePath());
        }

        final FilePath testFolder = new FilePath(testDirectory);
        final FilePath dctoolSh = testFolder.child("dc/dctool.sh");
        dctoolSh.write("#!/bin/sh\necho TEST > dtool.out", UTF_8);
        dctoolSh.chmod(Integer.parseInt("0700", OCTAL));

        final FilePath dctoolBat = testFolder.child("dc/dctool.bat");
        dctoolBat.write("@echo on\n\recho TEST > dtool.out\n\r", UTF_8);
        nwdiToolLibDir = String.format("%s%c", testDirectory.getAbsolutePath(), File.separatorChar);
        executor = createDCToolCommandExecutor();
    }

    /**
     * Create an executor object for dctool.
     * 
     * @return the newly created {@link DCToolCommandExecutor} object.
     * @throws IOException
     *             rethrown when creating the TaskListener for the used Launcher
     *             fails
     */
    private DCToolCommandExecutor createDCToolCommandExecutor() throws IOException {
        final BuildVariant buildVariant = new BuildVariant("default");
        buildVariant.addBuildOption(DevelopmentConfiguration.COM_SAP_JDK_HOME_PATH_KEY,
            JdkHomeAlias.Jdk131Home.toString());

        final JdkHomePaths paths = new JdkHomePaths();
        paths.add(JdkHomeAlias.Jdk131Home, testDirectory.getAbsolutePath());
        paths.add(JdkHomeAlias.Jdk142Home, testDirectory.getAbsolutePath());

        final Writer messages = new OutputStreamWriter(System.out);
        final Launcher launcher = new Launcher.LocalLauncher(new StreamTaskListener(messages));
        final DIToolDescriptor dcToolDescriptor =
            new DIToolDescriptor("developer", "secret", nwdiToolLibDir, "", paths);
        final DevelopmentConfiguration config = new DevelopmentConfiguration("Test");
        config.setBuildVariant(buildVariant);

        return new DCToolCommandExecutor(launcher, new FilePath(testDirectory), dcToolDescriptor, config);
    }

    /**
     * Clean up fixture.
     * 
     * @throws IOException
     */
    @After
    public void tearDown() throws IOException {
        executor = null;
        Util.deleteRecursive(testDirectory);
        testDirectory = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.dc.DCToolCommandExecutor#execute(org.arachna.netweaver.tools.DIToolCommandBuilder)}
     * .
     */
    @Test
    public final void testExecute() {
        try {
            executor = createDCToolCommandExecutor();
            executor.execute(new DummyDCToolCommandBuilder());
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
            // fail(e.getMessage());
        }
        catch (final InterruptedException e) {
            fail(e.getMessage());
        }
    }

    /**
     * dummy DCToolCommandBuild used for testing.
     * 
     * @author Dirk Weigenand
     */
    private static final class DummyDCToolCommandBuilder implements DIToolCommandBuilder {
        /**
         * {@inheritDoc}
         */
        public List<String> execute() {
            final List<String> commands = new ArrayList<String>();
            commands.add("xxx;");

            return commands;
        }
    }
}
