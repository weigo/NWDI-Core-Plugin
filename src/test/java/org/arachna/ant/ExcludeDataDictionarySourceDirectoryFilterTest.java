/**
 *
 */
package org.arachna.ant;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ExcludeDataDictionarySourceDirectoryFilterTest}.
 * 
 * @author Dirk Weigenand
 */
public class ExcludeDataDictionarySourceDirectoryFilterTest {
    /**
     * instance under test.
     */
    private SourceDirectoryFilter filter;

    /**
     * Set up fixture.
     */
    @Before
    public void setUp() {
        filter = new ExcludeDataDictionarySourceDirectoryFilter();
    }

    /**
     * Tear down fixture.
     */
    @After
    public void tearDown() {
        filter = null;
    }

    /**
     * Test method for {@link org.arachna.ant.ExcludeDataDictionarySourceDirectoryFilter#accept(java.lang.String)} .
     */
    @Test
    public final void testDoNotAcceptGenDdicDatatypesFolderUnixStyle() {
        assertThat(filter.accept("/tmp/gen_ddic/datatypes"), not(equalTo(true)));
    }

    /**
     * Test method for {@link org.arachna.ant.ExcludeDataDictionarySourceDirectoryFilter#accept(java.lang.String)} .
     */
    @Test
    public final void testDoNotAcceptGenDdicDatatypesFolderWindowsStyle() {
        assertThat(filter.accept("C:\\tmp\\gen_ddic\\datatypes"), not(equalTo(true)));
    }

    /**
     * Test method for {@link org.arachna.ant.ExcludeDataDictionarySourceDirectoryFilter#accept(java.lang.String)} .
     */
    @Test
    public final void testDoAcceptGenWdpFolder() {
        assertThat(filter.accept("C:\\tmp\\gen_wdp\\src\\packages"), equalTo(true));
    }
}
