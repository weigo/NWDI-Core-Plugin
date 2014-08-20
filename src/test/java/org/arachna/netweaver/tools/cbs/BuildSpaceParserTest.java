/**
 *
 */
package org.arachna.netweaver.tools.cbs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import hudson.Util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Collection;

import org.junit.Test;

/**
 * Unittest for {@link BuildSpaceParser}.
 *
 * @author Dirk Weigenand
 */
public class BuildSpaceParserTest {
    /**
     * build spaces that are expected to be read from sample output of listbuildspaces command.
     */
    private static final String[] EXPECTED_BUILDSPACES = { "DI0_Example_D", "DI0_Example1_D", "JDI_THECO50_D" };

    /**
     * Test method for {@link org.arachna.netweaver.tools.cbs.BuildSpaceParser#parse()}.
     */
    @Test
    public final void testParseCbsTool70Output() {
        final Collection<String> buildSpaces = getBuildSpaces("CbsToolListBuildSpaces70.txt");
        assertThat(buildSpaces.size(), equalTo(EXPECTED_BUILDSPACES.length));
        assertThat(buildSpaces, hasItems(EXPECTED_BUILDSPACES));
    }

    /**
     * Test method for {@link org.arachna.netweaver.tools.cbs.BuildSpaceParser#parse()}.
     */
    @Test
    public final void testParseCbsTool71PlusPlusOutput() {
        final Collection<String> buildSpaces = getBuildSpaces("CbsToolListBuildSpaces71pp.txt");
        assertThat(buildSpaces.size(), equalTo(EXPECTED_BUILDSPACES.length));
        assertThat(buildSpaces, hasItems(EXPECTED_BUILDSPACES));
    }

    @Test
    public final void testParseCbsTool731SP12Output() {
        final Collection<String> buildSpaces = getBuildSpaces("CbsToolListBuildSpaces7.31.SP12.txt");
        final String expected[] = new String[] { "COLL_IK2_D", "COLL_IKPROD_D", "COLL_SEP_D" };
        assertThat(buildSpaces.size(), equalTo(expected.length));
        assertThat(buildSpaces, hasItems(expected));
    }

    /**
     * Read in the given sample output of listbuildspaces command and return the names of development build spaces parsed from it.
     *
     * @param resourceName
     *            name of sample output file.
     * @return list of names of development build spaces.
     */
    private Collection<String> getBuildSpaces(final String resourceName) {
        final StringWriter result = new StringWriter();

        try {
            Util.copyStreamAndClose(
                new InputStreamReader(this.getClass().getResourceAsStream("/org/arachna/netweaver/tools/cbs/" + resourceName)), result);
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }

        return new BuildSpaceParser(result.toString()).parse();
    }
}
