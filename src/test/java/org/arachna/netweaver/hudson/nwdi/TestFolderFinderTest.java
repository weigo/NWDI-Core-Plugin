/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

/**
 * JUnit tests for {@link TestFolderFinder}.
 * 
 * @author Dirk Weigenand
 */
public class TestFolderFinderTest {
    /**
     * Test method for {@link org.arachna.netweaver.hudson.nwdi.TestFolderFinder#isTestFolder(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testIsTestFolderWithJUnit3Source() {
        final TestFolderFinderFromRessources finder =
            new TestFolderFinderFromRessources("/org/arachna/netweaver/hudson/nwdi/Junit3Test.java");
        assertThat(finder.isTestFolder(null, null), equalTo(true));
    }

    /**
     * Test method for {@link org.arachna.netweaver.hudson.nwdi.TestFolderFinder#isTestFolder(java.lang.String, java.lang.String)}.
     */
    @Test
    public final void testIsTestFolderWithJUnit4Source() {
        final TestFolderFinderFromRessources finder =
            new TestFolderFinderFromRessources("/org/arachna/netweaver/hudson/nwdi/Junit4Test.java");
        assertThat(finder.isTestFolder(null, null), equalTo(true));
    }

    /**
     * Overwrite <code>getJavaSources(final String encoding, final String sourceFolder)</code> to decouple from file system for testing
     * purposes.
     * 
     * @author Dirk Weigenand
     */
    class TestFolderFinderFromRessources extends TestFolderFinder {
        /**
         * Name of single resource to return for testing.
         */
        private final String resource;

        /**
         * Create new instance with resource name for testing.
         * 
         * @param resource
         *            resource name for testing.
         */
        TestFolderFinderFromRessources(final String resource) {
            this.resource = resource;
        }

        @Override
        protected List<TestFolderFinder.FileDescriptor> getJavaSources(final String encoding, final String sourceFolder) {
            return Arrays.asList((FileDescriptor)new RessourceDescriptor(sourceFolder + resource, getClass().getResourceAsStream(resource)));
        }

        class RessourceDescriptor extends TestFolderFinder.FileDescriptor {
            private String resource;
            private InputStream content;

            RessourceDescriptor(File file) {
                super(file);
                // TODO Auto-generated constructor stub
            }

            RessourceDescriptor(String resource, InputStream content) {
                super(null);
                this.resource = resource;
                this.content = content;
            }

            @Override
            InputStream getContent() throws IOException {
                return content;
            }

            @Override
            String getAbsolutePath() {
                return resource;
            }
        }
    }
}
