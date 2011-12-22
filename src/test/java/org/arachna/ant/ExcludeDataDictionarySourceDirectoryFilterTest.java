/**
 *
 */
package org.arachna.ant;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 */
public class ExcludeDataDictionarySourceDirectoryFilterTest {
    private SourceDirectoryFilter filter;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        this.filter = new ExcludeDataDictionarySourceDirectoryFilter();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        this.filter = null;
    }

    /**
     * Test method for
     * {@link org.arachna.ant.ExcludeDataDictionarySourceDirectoryFilter#accept(java.lang.String)}
     * .
     */
    @Test
    public final void testDoNotAcceptGen_ddicDatatypesFolderUnixStyle() {
        assertThat(this.filter.accept("/tmp/gen_ddic/datatypes"), not(equalTo(true)));
    }

    /**
     * Test method for
     * {@link org.arachna.ant.ExcludeDataDictionarySourceDirectoryFilter#accept(java.lang.String)}
     * .
     */
    @Test
    public final void testDoNotAcceptGen_ddicDatatypesFolderWindowsStyle() {
        assertThat(this.filter.accept("C:\\tmp\\gen_ddic\\datatypes"), not(equalTo(true)));
    }

    /**
     * Test method for
     * {@link org.arachna.ant.ExcludeDataDictionarySourceDirectoryFilter#accept(java.lang.String)}
     * .
     */
    @Test
    public final void testDoAcceptGen_WdpFolder() {
        assertThat(this.filter.accept("C:\\tmp\\gen_wdp\\src\\packages"), equalTo(true));
    }
}
