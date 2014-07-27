/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStreamReader;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for ListCompartmentsReader.
 * 
 * @author Dirk Weigenand
 */
public class ListCompartmentsReaderTest {
    /**
     * instance under test.
     */
    private ListCompartmentsReader reader;

    /**
     * development configuration to register read compartments with.
     */
    private DevelopmentConfiguration developmentConfiguration;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        developmentConfiguration = new DevelopmentConfiguration("DI0_XMPL_D");
        reader = new ListCompartmentsReader(developmentConfiguration);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
        reader = null;
        developmentConfiguration = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.cbs.ListCompartmentsReader#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testExecute() {
        reader.execute(new InputStreamReader(getClass().getResourceAsStream(
            "/org/arachna/netweaver/tools/cbs/CbsToolListCompartments70.txt")));

        final String[] compartments =
            new String[] { "arachna.org_KM_SUPPORT_1", "arachna.org_SPRING_JPA_SUPPORT_1",
                "example.org_UNIT_TEST_SUPPORT_1", "sap.com_ENGFACADE_1", "sap.com_KMC-CM_1", "sap.com_SAP_BUILDT_1",
                "springsource.org_SPRING_FRAMEWORK_1" };
        final CompartmentState[] states =
            new CompartmentState[] { CompartmentState.Source, CompartmentState.Source, CompartmentState.Source,
                CompartmentState.Archive, CompartmentState.Archive, CompartmentState.Archive, CompartmentState.Source };

        for (int i = 0; i < compartments.length; i++) {
            final Compartment compartment = developmentConfiguration.getCompartment(compartments[i]);

            assertThat(compartment, notNullValue());
            assertThat(compartment.getName(), equalTo(compartments[i]));
            assertThat(compartment.getState(), equalTo(states[i]));
        }
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.cbs.ListCompartmentsReader#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testRegisterCompartmentIfLineMatchesForCompartmentInArchiveState() {
        reader.registerCompartmentIfLineMatches("sap.com_SAP_BUILDT_1                    (archive state)");
        final Compartment compartment = developmentConfiguration.getCompartment("sap.com_SAP_BUILDT_1");
        assertThat(compartment, notNullValue());
        assertThat(compartment.getState(), equalTo(CompartmentState.Archive));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.cbs.ListCompartmentsReader#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testRegisterCompartmentIfLineMatchesForCompartmentInSourceState() {
        reader.registerCompartmentIfLineMatches("springsource.org_SPRING_FRAMEWORK_1     (source  state)");
        final Compartment compartment = developmentConfiguration.getCompartment("springsource.org_SPRING_FRAMEWORK_1");
        assertThat(compartment, notNullValue());
        assertThat(compartment.getState(), equalTo(CompartmentState.Source));
    }
}
