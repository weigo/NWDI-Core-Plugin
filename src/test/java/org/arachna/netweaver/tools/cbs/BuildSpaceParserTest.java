/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import hudson.Util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unittest for {@link BuildSpaceParser}.
 * 
 * @author Dirk Weigenand
 */
public class BuildSpaceParserTest {
    /**
     * Instance under test.
     */
    private BuildSpaceParser parser;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.cbs.BuildSpaceParser#parse()}.
     * 
     * @throws IOException
     */
    @Test
    public final void testParseCbsTool70Output() throws IOException {
        initParser("CbsToolListBuildSpaces70.txt");
        assertThat(parser.parse(), hasItems("DI0_Example_D", "DI0_Example1_D", "JDI_THECO50_D"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.cbs.BuildSpaceParser#parse()}.
     * 
     * @throws IOException
     */
    @Test
    public final void testParseCbsTool71PlusPlusOutput() throws IOException {
        initParser("CbsToolListBuildSpaces71pp.txt");
        assertThat(parser.parse(), hasItems("DI0_Example_D", "DI0_Example1_D"));
    }

    private void initParser(final String resourceName) throws IOException {
        final StringWriter result = new StringWriter();
        Util.copyStreamAndClose(
            new InputStreamReader(this.getClass().getResourceAsStream(
                "/org/arachna/netweaver/tools/cbs/" + resourceName)), result);

        parser = new BuildSpaceParser(result.toString());
    }
}
