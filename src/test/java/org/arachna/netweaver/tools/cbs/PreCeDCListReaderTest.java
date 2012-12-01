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
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link PreCeDCListReader}.
 * 
 * @author Dirk Weigenand
 */
public class PreCeDCListReaderTest {
    /**
     * 
     */
    private static final String EXAMPLE_ORG_UNIT_TEST_SUPPORT_1 = "example.org_UNIT_TEST_SUPPORT_1";

    /**
     * 
     */
    private static final String ARACHNA_ORG_SPRING_JPA_SUPPORT_1 = "arachna.org_SPRING_JPA_SUPPORT_1";

    /**
     * 
     */
    private static final String ARACHNA_ORG_KM_SUPPORT_1 = "arachna.org_KM_SUPPORT_1";

    /**
     * instance under test.
     */
    private PreCeDCListReader reader;

    /**
     * development configuration to add development components to.
     */
    private DevelopmentConfiguration config;

    /**
     * registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     * 
     */
    @Before
    public void setUp() {
        config = new DevelopmentConfiguration("DI0_XMPL_D");
        config.add(Compartment.create(ARACHNA_ORG_KM_SUPPORT_1, CompartmentState.Source));
        config.add(Compartment.create(ARACHNA_ORG_SPRING_JPA_SUPPORT_1, CompartmentState.Source));
        config.add(Compartment.create(EXAMPLE_ORG_UNIT_TEST_SUPPORT_1, CompartmentState.Source));
        dcFactory = new DevelopmentComponentFactory();
        reader = new PreCeDCListReader(config, dcFactory);
        reader.execute(new InputStreamReader(getClass().getResourceAsStream(
            "/org/arachna/netweaver/tools/cbs/CbsToolListDcs70.txt")));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
        config = null;
        dcFactory = null;
        reader = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.cbs.PreCeDCListReader#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testReadSCKMSupport() {
        final Compartment compartment = config.getCompartment(ARACHNA_ORG_KM_SUPPORT_1);

        assertThatCompartmentHasDC(compartment, "arachna.org", "kmhelper");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.cbs.PreCeDCListReader#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testReadSCSpringJPASupport() {
        final Compartment compartment = config.getCompartment(ARACHNA_ORG_SPRING_JPA_SUPPORT_1);

        assertThatCompartmentHasDC(compartment, "arachna.org", "orpersistence");
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.tools.cbs.PreCeDCListReader#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testReadSCUnitTestSupport() {
        final Compartment compartment = config.getCompartment(EXAMPLE_ORG_UNIT_TEST_SUPPORT_1);

        assertThatCompartmentHasDC(compartment, "example.org", "sc/unit/test/support");
        assertThatCompartmentHasDC(compartment, "junit.org", "junit");
        assertThatCompartmentHasDC(compartment, "mockito.org", "mockito");
        assertThatCompartmentHasDC(compartment, "hamcrest.org", "hamcrest");
    }

    /**
     * @param compartment
     */
    private void assertThatCompartmentHasDC(final Compartment compartment, final String vendor, final String dcName) {
        final DevelopmentComponent developmentComponent = dcFactory.get(vendor, dcName);
        assertThat(developmentComponent, notNullValue());
        assertThat(developmentComponent.getCompartment(), equalTo(compartment));
    }

}
