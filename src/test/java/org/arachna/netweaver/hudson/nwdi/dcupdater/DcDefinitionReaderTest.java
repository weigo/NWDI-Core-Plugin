/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;

import java.io.InputStreamReader;
import java.io.Reader;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.xml.DigesterHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link DcDefinitionRulesModuleProducer}.
 * 
 * @author Dirk Weigenand
 */
public class DcDefinitionReaderTest {
    /**
     * instance under test.
     */
    private DcDefinitionRulesModuleProducer dcDefReader;

    /**
     * example development component for testing {@link DcDefinitionRulesModuleProducer}
     * correctness.
     */
    private DevelopmentComponent component;

    /**
     * Set up fixture.
     */
    @Before
    public void setUp() {
        dcDefReader = new DcDefinitionRulesModuleProducer();
        final DigesterHelper<DevelopmentComponent> digesterHelper =
            new DigesterHelper<DevelopmentComponent>(dcDefReader);
        component = new DevelopmentComponent("", "");
        digesterHelper.update(getDcDef(), component);
    }

    /**
     * Tear down fixture.
     */
    @After
    public void tearDown() {
        dcDefReader = null;
        component = null;
    }

    /**
     * Test method for
     * {@link DcDefinitionReader1#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void testDCNameRule() {
        assertThat(component.getName(), equalTo("DC1"));
    }

    /**
     * Test method for
     * {@link DcDefinitionReader1#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void testDCVendorRule() {
        assertThat(component.getVendor(), equalTo("example.com"));
    }

    /**
     * Test method for
     * {@link DcDefinitionReader1#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void testDCCaptionRule() {
        assertThat(component.getCaption(), equalTo("example component caption"));
    }

    /**
     * Test method for
     * {@link DcDefinitionReader1#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void testDCDescriptionRule() {
        assertThat(component.getDescription(), equalTo("example component description"));
    }

    /**
     * Test method for
     * {@link DcDefinitionReader1#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void testDCTypeRule() {
        assertThat(component.getType(), equalTo(DevelopmentComponentType.fromString("J2EE", "EJBModule")));
    }

    /**
     * Test method for
     * {@link DcDefinitionReader1#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void testDCBuildPluginRule() {
        assertThat(component.getBuildPlugin().getVendor(), equalTo("sap.com"));
        assertThat(component.getBuildPlugin().getComponentName(), equalTo("tc/bi/bp/ejbmodule"));
        assertThat(component.getBuildPlugin().getName(), equalTo("ejb"));
    }

    /**
     * Test method for
     * {@link DcDefinitionReader1#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void testDCFoldersRule() {
        assertThat(component.getSourceFolders(), hasSize(3));
        assertThat(component.getSourceFolders(), containsInAnyOrder(".apt_generated", "ejbModule", "test"));
    }

    /**
     * Test method for
     * {@link DcDefinitionReader1#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void testDCAddDependencyRule() {
        assertThat(component.getUsedDevelopmentComponents(), hasSize(2));

        for (final PublicPartReference reference : component.getUsedDevelopmentComponents()) {
            if (isReference(reference, "apache.org", "jee/commons")) {
                assertProperties(reference, "api", true, true, false);
            }
            else if (isReference(reference, "apache.org", "velocity")) {
                assertProperties(reference, "assembly", true, false, false);
            }
        }
    }

    /**
     * @param reference
     */
    protected void assertProperties(final PublicPartReference reference, final String publicPartName,
        final boolean atBuildTime, final boolean atRunTime, final boolean atDeployTime) {
        assertThat(publicPartName, equalTo(reference.getName()));
        assertThat("atBuildTime", reference.isAtBuildTime(), equalTo(atBuildTime));
        assertThat("atRunTime", reference.isAtRunTime(), equalTo(atRunTime));
        assertThat("atDeployTime", reference.isAtDeployTime(), equalTo(atDeployTime));
    }

    /**
     * @param reference
     * @return
     */
    protected boolean isReference(final PublicPartReference reference, final String vendor, final String componentName) {
        return componentName.equals(reference.getComponentName()) && vendor.equals(reference.getVendor());
    }

    /**
     * Get example input from class path.
     * 
     * @return reader object with example input.
     */
    private Reader getDcDef() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/hudson/nwdi/dcupdater/example.dcdef"));
    }
}
