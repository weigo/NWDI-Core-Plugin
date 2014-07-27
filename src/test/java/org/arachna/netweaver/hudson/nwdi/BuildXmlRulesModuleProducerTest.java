/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.xml.DigesterHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link BuildXmlRulesModuleProducer}.
 * 
 * @author Dirk Weigenand
 */
public class BuildXmlRulesModuleProducerTest {
    /**
     * 
     */
    private static final String UTF_8 = "UTF-8";
    /**
     * example development to observe changes.
     */
    private DevelopmentComponent component;

    /**
     * Set up fixture.
     */
    @Before
    public void setUp() {
        component = new DevelopmentComponent("", "");
        new DigesterHelper<DevelopmentComponent>(new BuildXmlRulesModuleProducer(new FakeTestFolderFinder())).update(
            getBuildXml(), component);

    }

    /**
     * Tear down fixture.
     */
    @After
    public void tearDown() {
        component = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.BuildXmlRulesModuleProducer#getRulesModule()}
     * .
     */
    @Test
    public final void assertParsingResultsInCorrectSourceEndocing() {
        assertThat(component.getSourceEncoding(), equalTo(UTF_8));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.BuildXmlRulesModuleProducer#getRulesModule()}
     * .
     */
    @Test
    public final void assertParsingResultsInCorrectOutputFolder() {
        assertThat(component.getOutputFolder(),
            equalTo("/home/weigo/tmp/hudson/workspace/EXAMPLE_TRACK/.dtc/t/9EE6C210A2FB88E24A923C37CD81FFCB/classes"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.BuildXmlRulesModuleProducer#getRulesModule()}
     * .
     */
    @Test
    public final void assertParsingResultsInCorrectSourceFolders() {
        assertThat(
            component.getSourceFolders(),
            containsInAnyOrder("/home/weigo/tmp/hudson/workspace/EXAMPLE_TRACK/.dtc/DCs/arachna.org/spring_sap_jpa_support/_comp/src"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.nwdi.BuildXmlRulesModuleProducer#getRulesModule()}
     * .
     */
    @Test
    public final void assertParsingResultsInCorrectTestSourceFolders() {
        assertThat(
            component.getTestSourceFolders(),
            containsInAnyOrder("/home/weigo/tmp/hudson/workspace/EXAMPLE_TRACK/.dtc/DCs/arachna.org/spring_sap_jpa_support/_comp/test"));
    }

    private Reader getBuildXml() {
        return new InputStreamReader(this.getClass()
            .getResourceAsStream("/org/arachna/netweaver/hudson/nwdi/build.xml"), Charset.forName(UTF_8));
    }

    private class FakeTestFolderFinder extends TestFolderFinder {
        @Override
        boolean isTestFolder(final String encoding, final String sourceFolder) {
            boolean retVal = false;

            if ("/home/weigo/tmp/hudson/workspace/EXAMPLE_TRACK/.dtc/DCs/arachna.org/spring_sap_jpa_support/_comp/test"
                .equals(sourceFolder)) {
                retVal = true;
            }

            return retVal;
        }
    }
}
