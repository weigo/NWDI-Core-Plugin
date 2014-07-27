/**
 * 
 */
package org.arachna.netweaver.tools;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import org.arachna.netweaver.dc.types.JdkHomeAlias;
import org.arachna.netweaver.dc.types.JdkHomePaths;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unittests for {@link DIToolDescriptor}.
 * 
 * @author Dirk Weigenand
 */
public class DIToolDescriptorTest {
    /**
     * instance under test.
     */
    private DIToolDescriptor descriptor;

    /**
     * Jdk home paths registered with the descriptor.
     */
    private JdkHomePaths paths;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        paths = new JdkHomePaths();
        descriptor = new DIToolDescriptor(null, null, null, null, paths);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
        descriptor = null;
    }

    /**
     * Should return the java.home property without '/jre', when no home registered for the given alias.
     */
    @Test
    public final void assertGetJavaHomeReturnsJvmHomeWhenNoJdkHomePathIsRegisteredForAGivenAlias() {
        final File javaHome = new File(System.getProperty("java.home"));
        assertThat(descriptor.getJavaHome(JdkHomeAlias.Jdk131Home), equalTo(javaHome.getParent()));
    }

    /**
     * Should return the registered path for the given alias.
     */
    @Test
    public final void assertGetJavaHomeReturnsPathForRegisteredAlias() {
        final String path = "/opt/jdk1.3.1";
        paths.add(JdkHomeAlias.Jdk131Home, path);

        assertThat(descriptor.getJavaHome(JdkHomeAlias.Jdk131Home), equalTo(path));
    }
}
