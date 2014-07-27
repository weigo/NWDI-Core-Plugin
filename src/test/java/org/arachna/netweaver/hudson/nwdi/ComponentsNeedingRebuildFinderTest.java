/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.matchers.JUnitMatchers.hasItems;

import java.util.Arrays;
import java.util.Collection;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link ComponentsNeedingRebuildFinder}.
 * 
 * @author Dirk Weigenand
 */
public class ComponentsNeedingRebuildFinderTest {
    /**
     * Registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     * instance under test.
     */
    private ComponentsNeedingRebuildFinder finder;

    /**
     */
    @Before
    public void setUp() {
        this.dcFactory = ExampleDevelopmentComponentFactory.create();
        this.finder = new ComponentsNeedingRebuildFinder();
    }

    /**
     */
    @After
    public void tearDown() {
        this.dcFactory = null;
        this.finder = null;
    }

    /**
     * Test method for {@link ComponentsNeedingRebuildFinder#calculateDevelopmentComponentsThatNeedRebuilding(java.util.Collection)} .
     */
    @Test
    public final void testCalculateDevelopmentComponentsThatNeedRebuildingWithStandAloneDC() {
        final DevelopmentComponent developmentComponent =
            this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JDBC);
        final Collection<DevelopmentComponent> result =
            this.finder
                .calculateDevelopmentComponentsThatNeedRebuilding(Arrays.asList(new DevelopmentComponent[] { developmentComponent }));

        assertThat(1, equalTo(result.size()));
        assertThat(developmentComponent, equalTo(result.iterator().next()));
    }

    /**
     * Test method for {@link ComponentsNeedingRebuildFinder#calculateDevelopmentComponentsThatNeedRebuilding(java.util.Collection)} .
     */
    @Test
    public final void testCalculateDevelopmentComponentsThatNeedRebuildingWithDCWithOneUsage() {
        final DevelopmentComponent libJetm =
            this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JETM);
        final DevelopmentComponent libJetmHelper =
            this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JETM_HELPER);
        final DevelopmentComponent libJeeJetm =
            this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JEE_JETM);
        final DevelopmentComponent[] expected = new DevelopmentComponent[] { libJetm, libJetmHelper, libJeeJetm };
        final Collection<DevelopmentComponent> result =
            this.finder.calculateDevelopmentComponentsThatNeedRebuilding(Arrays.asList(new DevelopmentComponent[] { libJetm }));

        assertThat(result, hasSize(expected.length));
        assertThat(result, hasItems(expected));
    }

    /**
     * Test method for {@link ComponentsNeedingRebuildFinder#calculateDevelopmentComponentsThatNeedRebuilding(java.util.Collection)} .
     */
    @Test
    public final void testCalculateDevelopmentComponentsThatNeedRebuildingWithTwoBaseDCs() {
        final DevelopmentComponent libJunit =
            this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JUNIT);
        final DevelopmentComponent libJetm =
            this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JETM);
        final DevelopmentComponent libJetmHelper =
            this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JETM_HELPER);
        final DevelopmentComponent libJeeJetm =
            this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JEE_JETM);
        final DevelopmentComponent[] expected = new DevelopmentComponent[] { libJunit, libJetm, libJetmHelper, libJeeJetm };
        final Collection<DevelopmentComponent> result =
            this.finder.calculateDevelopmentComponentsThatNeedRebuilding(Arrays.asList(new DevelopmentComponent[] { libJunit, libJetm }));

        assertThat(result, hasItems(expected));
        assertThat(result, hasSize(expected.length));
    }
}
