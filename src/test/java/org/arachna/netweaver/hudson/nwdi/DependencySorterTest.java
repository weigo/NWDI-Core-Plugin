package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.Arrays;
import java.util.Collection;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link DependencySorter}.
 * 
 * @author g526521
 */
public class DependencySorterTest {
    /**
     * registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     *
     */
    @Before
    public void setUp() {
        this.dcFactory = ExampleDevelopmentComponentFactory.create();
    }

    /**
     * reset <code>dcFactory</code> after each test.
     */
    @After
    public void tearDown() {
        this.dcFactory = null;
    }

    /**
     * check whether the correct build sequence was computed.
     */
    @Test
    public final void testDetermineBuildSequenceForLibITextAndDependencies() {
        final DevelopmentComponent libJunit =
            this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JUNIT);

        final DevelopmentComponent[] components = getDevelopmentComponentsBuildSequence(new DevelopmentComponent[] { libJunit });
        assertThat(3, equalTo(components.length));
        assertThat(libJunit, equalTo(components[0]));
        assertThat(this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JETM_HELPER),
            equalTo(components[1]));
        assertThat(this.dcFactory.get(ExampleDevelopmentComponentFactory.EXAMPLE_COM, ExampleDevelopmentComponentFactory.LIB_JEE_JETM),
            equalTo(components[2]));
    }

    /**
     * Get build sequence of development components.
     * 
     * @param base
     *            development components that have been changed in recent activities.
     * @return build sequence of development components.
     */
    private DevelopmentComponent[] getDevelopmentComponentsBuildSequence(final DevelopmentComponent[] base) {
        final ComponentsNeedingRebuildFinder finder = new ComponentsNeedingRebuildFinder();
        final DependencySorter sorter =
            new DependencySorter(this.dcFactory, finder.calculateDevelopmentComponentsThatNeedRebuilding(Arrays.asList(base)));

        final Collection<DevelopmentComponent> buildSequence = sorter.determineBuildSequence();
        return buildSequence.toArray(new DevelopmentComponent[buildSequence.size()]);
    }
}
