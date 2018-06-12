package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.io.InputStreamReader;
import java.util.Arrays;

import org.arachna.netweaver.dc.config.DevelopmentConfigurationReader;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.netweaver.hudson.nwdi.TopoSortResult.CircularDependency;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit test for {@link DependencySorter}.
 *
 * @author Dirk Weigenand
 */
public class TopoSortTest {
    /**
     * registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     * Instance under test.
     */
    private TopoSort sorter;

    /**
     * A compartment in source state.
     */
    private Compartment sourceCompartment;

    /**
     *
     */
    @Before
    public void setUp() {
        dcFactory = new DevelopmentComponentFactory();
        sorter = new TopoSort(dcFactory, System.err);
        sourceCompartment = Compartment.create("example.org_COMPARTMENT_1", CompartmentState.Source);
    }

    /**
     * reset <code>dcFactory</code> after each test.
     */
    @After
    public void tearDown() {
        dcFactory = null;
    }

    private TopoSortResult sort(final DevelopmentComponent... component) {
        dcFactory.updateUsingDCs();
        return sorter.sort(Arrays.asList(component));
    }

    //@Test
    public void noDCs() {
        final TopoSortResult result = sort();
        assertThat(result.getDevelopmentComponents(), empty());
        assertThat(result.getCircularDependencies(), empty());
    }

    //@Test
    public void oneDC() {
        final DevelopmentComponent component = dcFactory.create("example.org", "one", DevelopmentComponentType.Java);
        sourceCompartment.add(component);

        final TopoSortResult result = sort(component);
        assertThat(result.getDevelopmentComponents(), hasItem(component));
        assertThat(result.getCircularDependencies(), empty());
    }

//    @Test
    public void oneDCWithCircularDepToItself() {
        final DevelopmentComponent component = dcFactory.create("example.org", "one", DevelopmentComponentType.Java);
        sourceCompartment.add(component);
        component.add(new PublicPartReference("example.org", "one"));

        final TopoSortResult result = sort(component);
        assertThat(result.getDevelopmentComponents(), empty());
        assertThat(result.getCircularDependencies(), hasItem(new CircularDependency(component, component)));
    }

    //@Test
    public void twoDCs() {
        final DevelopmentComponent one = dcFactory.create("example.org", "one", DevelopmentComponentType.Java);
        sourceCompartment.add(one);
        final DevelopmentComponent two = dcFactory.create("example.org", "two", DevelopmentComponentType.Java);
        sourceCompartment.add(two);

        final TopoSortResult result = sort(one, two);
        assertThat(result.getDevelopmentComponents(), hasItem(one));
        assertThat(result.getDevelopmentComponents(), hasItem(two));
        assertThat(result.getCircularDependencies(), empty());
    }

    //@Test
    public void twoDCsDependingOnEachOther() {
        final DevelopmentComponent one = dcFactory.create("example.org", "one", DevelopmentComponentType.Java);
        sourceCompartment.add(one);
        one.add(new PublicPartReference("example.org", "two"));

        final DevelopmentComponent two = dcFactory.create("example.org", "two", DevelopmentComponentType.Java);
        sourceCompartment.add(two);
        two.add(new PublicPartReference("example.org", "one"));

        final TopoSortResult result = sort(one, two);
        assertThat(result.getDevelopmentComponents(), empty());
        assertThat(result.getCircularDependencies(), hasItems(new CircularDependency(one, two), new CircularDependency(two, one)));
    }

    //@Test
    public void threeDCs() {
        final DevelopmentComponent one = dcFactory.create("example.org", "one", DevelopmentComponentType.Java);
        sourceCompartment.add(one);

        final DevelopmentComponent two = dcFactory.create("example.org", "two", DevelopmentComponentType.Java);
        sourceCompartment.add(two);

        final DevelopmentComponent three = dcFactory.create("example.org", "three", DevelopmentComponentType.Java);
        sourceCompartment.add(three);
        three.add(new PublicPartReference("example.org", "one"));
        three.add(new PublicPartReference("example.org", "two"));

        final TopoSortResult result = sort(one, two);
        assertThat(result.getDevelopmentComponents(), hasItems(one, two, three));
        assertThat(result.getCircularDependencies(), empty());
    }

    //@Test
    public void fourDCs() {
        final DevelopmentComponent one = dcFactory.create("example.org", "one", DevelopmentComponentType.Java);
        sourceCompartment.add(one);

        final DevelopmentComponent two = dcFactory.create("example.org", "two", DevelopmentComponentType.Java);
        sourceCompartment.add(two);

        final DevelopmentComponent three = dcFactory.create("example.org", "three", DevelopmentComponentType.Java);
        sourceCompartment.add(three);
        three.add(new PublicPartReference("example.org", "one"));
        three.add(new PublicPartReference("example.org", "two"));

        final DevelopmentComponent four = dcFactory.create("example.org", "four", DevelopmentComponentType.Java);
        sourceCompartment.add(four);
        four.add(new PublicPartReference("example.org", "three"));

        final TopoSortResult result = sort(one, two);
        assertThat(result.getDevelopmentComponents(), hasItems(one, two, three, four));
        assertThat(result.getCircularDependencies(), empty());
    }

    @Test
    public void testXXX() {
        DevelopmentConfigurationReader reader = new DevelopmentConfigurationReader(this.dcFactory);
        reader.execute(new InputStreamReader(this.getClass().getResourceAsStream("DevelopmentConfiguration.xml")));
        DevelopmentComponent component = this.dcFactory.get("gisa.de", "portal/framework/navigation/lib");
        component.setNeedsRebuild(true);
        TopoSortResult result = sort(component);
        System.err.println(result);
    }
}
