/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIn.isIn;

import java.io.InputStreamReader;
import java.io.Reader;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.xml.DigesterHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link PortalApplicationConfigurationRulesModuleProducer}.
 * 
 * @author Dirk Weigenand
 */
public class PortalApplicationConfigurationReaderTest {
    /**
     * 
     */
    private static final String VENDOR = "com.example";

    /**
     * 
     */
    private PortalApplicationConfigurationRulesModuleProducer configurationReader;

    /**
     * sample development component.
     */
    private DevelopmentComponent component;

    /**
     * set up fixture.
     */
    @Before
    public void setUp() {
        configurationReader = new PortalApplicationConfigurationRulesModuleProducer();
        final DigesterHelper<DevelopmentComponent> digesterHelper =
            new DigesterHelper<DevelopmentComponent>(configurationReader);
        component = new DevelopmentComponent("", "");
        digesterHelper.update(getPortalAppXml(), component);
    }

    /**
     * tear down fixture.
     */
    @After
    public void tearDown() {
        configurationReader = null;
        component = null;
    }

    /**
     * Test method for
     * {@link PortalApplicationConfigurationRulesModuleProducer#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void testExecute() {
        assertThat(component.getUsedDevelopmentComponents(), hasSize(4));
    }

    /**
     * Test method for
     * {@link PortalApplicationConfigurationRulesModuleProducer#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void assertReaderRecognizesApplicationReferences() {
        final PublicPartReference application = new PublicPartReference(VENDOR, "application");
        application.setAtRunTime();
        assertThat(application, isIn(component.getUsedDevelopmentComponents()));
    }

    /**
     * Test method for
     * {@link PortalApplicationConfigurationRulesModuleProducer#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void assertReaderRecognizesServiceReferences() {
        final PublicPartReference service = new PublicPartReference(VENDOR, "service");
        service.setAtRunTime();
        assertThat(service, isIn(component.getUsedDevelopmentComponents()));
    }

    /**
     * Test method for
     * {@link PortalApplicationConfigurationRulesModuleProducer#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void assertReaderRecognizesInterfaceReferences() {
        final PublicPartReference interfaze = new PublicPartReference(VENDOR, "interface");
        interfaze.setAtRunTime();
        assertThat(interfaze, isIn(component.getUsedDevelopmentComponents()));
    }

    /**
     * Test method for
     * {@link PortalApplicationConfigurationRulesModuleProducer#execute(org.arachna.netweaver.dc.types.DevelopmentComponent, java.io.Reader)}
     * .
     */
    @Test
    public final void assertReaderRecognizesLibraryReferences() {
        final PublicPartReference library = new PublicPartReference(VENDOR, "library");
        library.setAtRunTime();
        assertThat(library, isIn(component.getUsedDevelopmentComponents()));
    }

    /**
     * Get example input from class path.
     * 
     * @return reader object with example input.
     */
    private Reader getPortalAppXml() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/hudson/nwdi/dcupdater/portalapp.xml"));
    }
}
