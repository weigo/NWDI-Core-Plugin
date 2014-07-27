/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStreamReader;
import java.io.Reader;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.nwdi.DCBuildResultParser.BuildResults;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 * 
 */
public class DCBuildResultParserTest {
    /**
     * 
     */
    private static final String EXAMPLE_ORG = "example.org";

    /**
     * Instance under test.
     */
    private DCBuildResultParser buildResultParser;

    /**
     * development configuration to use for determining DCs and compartments.
     */
    private DevelopmentConfiguration config;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        config = new DevelopmentConfiguration("DI0_Example_D");
        final Compartment compartment = Compartment.create("example.org_EXAMPLE_SC_1", CompartmentState.Source);
        config.add(compartment);
        final DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();
        compartment.add(dcFactory.create(EXAMPLE_ORG, "sc/example_sc_description"));
        compartment.add(dcFactory.create(EXAMPLE_ORG, "example/dc1"));
        compartment.add(dcFactory.create(EXAMPLE_ORG, "example/dc2"));
        this.buildResultParser = new DCBuildResultParser(config);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
        this.buildResultParser = null;
    }

    @Test
    public void test() {
        final BuildResults results = buildResultParser.parse(getBuildResultText("DCBuildResultWithBuildErrors.txt"));

        assertThat(results.hasBuildErrors(), equalTo(true));
    }

    /**
     * @return
     */
    private Reader getBuildResultText(final String buildLog) {
        return new InputStreamReader(this.getClass().getResourceAsStream(String.format("/org/arachna/netweaver/hudson/nwdi/%s", buildLog)));
    }

}
