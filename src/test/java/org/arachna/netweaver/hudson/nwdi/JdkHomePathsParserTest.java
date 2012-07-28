/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

import org.arachna.netweaver.dc.types.JdkHomeAlias;
import org.arachna.netweaver.tools.dc.JdkHomePaths;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 * 
 */
public class JdkHomePathsParserTest {
    /**
     * path spec for jdk 5.
     */
    private static final String HOME_FOR_JDK1_5_0 = "/opt/jdk1.5.0";

    /**
     * path spec for jdk 5.
     */
    private static final String HOME_FOR_JDK1_4_2 = "/opt/jdk1.4.2";

    /**
     * path spec for jdk 5.
     */
    private static final String HOME_FOR_JDK1_3_1 = "/opt/jdk1.3.1";

    private JdkHomePathsParser parser;

    /**
     * Return the parsed paths.
     * 
     * @param pathSpec
     *            path specifications.
     * @return
     */
    private JdkHomePaths getPaths(String pathSpec) {
        parser = new JdkHomePathsParser(pathSpec);

        return parser.parse();
    }

    @Test
    public void testGetEmptyConfiguredJdkHomePaths() {
        final JdkHomePaths paths = this.getPaths("");
        assertThat(paths.getAliases(), hasSize(0));
    }

    @Test
    public void testGetConfiguredJdkHomePathsWithJdkOnePath() {
        final JdkHomePaths paths =
            this.getPaths(String.format("%s=%s", JdkHomeAlias.Jdk131Home.toString(), HOME_FOR_JDK1_3_1));
        assertThat(paths.getAliases(), hasSize(1));
        assertThat(HOME_FOR_JDK1_3_1, equalTo(paths.get(JdkHomeAlias.Jdk131Home)));
    }

    @Test
    public void testGetConfiguredJdkHomePathsWithJdkOnePathAndLooseFormatting() {
        final JdkHomePaths paths =
            this.getPaths(String.format("%s = %s", JdkHomeAlias.Jdk131Home.toString(), HOME_FOR_JDK1_3_1));
        assertThat(paths.getAliases(), hasSize(1));
        assertThat(HOME_FOR_JDK1_3_1, equalTo(paths.get(JdkHomeAlias.Jdk131Home)));
    }

    @Test
    public void testGetConfiguredJdkHomePathsWithSeveralJdks() {
        final JdkHomePaths paths =
            this.getPaths(String.format("%s=%s;%s=%s,%s=%s", JdkHomeAlias.Jdk131Home.toString(), HOME_FOR_JDK1_3_1,
                JdkHomeAlias.Jdk142Home, HOME_FOR_JDK1_4_2, JdkHomeAlias.Jdk150Home, HOME_FOR_JDK1_5_0));
        assertThat(paths.getAliases(), hasSize(3));
        assertThat(HOME_FOR_JDK1_3_1, equalTo(paths.get(JdkHomeAlias.Jdk131Home)));
        assertThat(HOME_FOR_JDK1_4_2, equalTo(paths.get(JdkHomeAlias.Jdk142Home)));
        assertThat(HOME_FOR_JDK1_5_0, equalTo(paths.get(JdkHomeAlias.Jdk150Home)));
    }

    @Test
    public void testGetConfiguredJdkHomePathsWithSeveralJdksAndLooseFormatting() {
        final JdkHomePaths paths =
            this.getPaths(String.format("%s= %s;%s=%s , %s = %s ", JdkHomeAlias.Jdk131Home.toString(),
                HOME_FOR_JDK1_3_1, JdkHomeAlias.Jdk142Home, HOME_FOR_JDK1_4_2, JdkHomeAlias.Jdk150Home,
                HOME_FOR_JDK1_5_0));
        assertThat(paths.getAliases(), hasSize(3));
        assertThat(HOME_FOR_JDK1_3_1, equalTo(paths.get(JdkHomeAlias.Jdk131Home)));
        assertThat(HOME_FOR_JDK1_4_2, equalTo(paths.get(JdkHomeAlias.Jdk142Home)));
        assertThat(HOME_FOR_JDK1_5_0, equalTo(paths.get(JdkHomeAlias.Jdk150Home)));
    }

    @Test
    public void testParsingCollectsInvalidJdkHomes() {
        this.getPaths("xyz");
        assertThat(this.parser.hasInvalidJdkHomeNames(), equalTo(true));
    }

    @Test
    public void testParserFormatsInvalidJdkHomes() {
        String expected = "xyz";
        this.getPaths(expected);
        assertThat(this.parser.getInvalidJdkHomeNames(), equalTo(expected));
    }

    @Test
    public void testParserFormatsMultipleInvalidJdkHomes() {
        String expected = "abc, xyz";
        this.getPaths("xyz=/opt/jdk1.5;abc=/opt/jdk1.6");
        assertThat(this.parser.getInvalidJdkHomeNames(), equalTo(expected));
    }
}
