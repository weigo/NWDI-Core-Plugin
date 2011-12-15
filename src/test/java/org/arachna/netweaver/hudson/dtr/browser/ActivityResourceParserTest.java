/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

import static org.hamcrest.CoreMatchers.is;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ActivityResourceParser}.
 * 
 * @author Dirk Weigenand
 */
public class ActivityResourceParserTest {
    /**
     * Registry for development components.
     */
    private DevelopmentComponentFactory developmentComponentFactory;

    /**
     * parser for activity resources.
     */
    private ActivityResourceParser activityResourceParser;

    /**
     * Activity to associate with the parsed resources.
     */
    private Activity activity;

    /**
     * compartment the activity belongs to.
     */
    private Compartment compartment;

    /**
     */
    @Before
    public void setUp() {
        this.developmentComponentFactory = new DevelopmentComponentFactory();
        compartment =
            new Compartment("EXAMPLE_SC1", CompartmentState.Source, "example.com", "", "example.com_EXAMPLE_SC1");
        this.activity = new Activity(compartment, "URL", new Principal("name"), "description", new Date());
        this.activityResourceParser = new ActivityResourceParser(this.developmentComponentFactory, this.activity);
    }

    @Test
    public void testActivityResourceExctraction() {
        final Collection<ActivityResource> resources = this.getActivityResources("ResourceList.html");
        assertNotNull(resources);
        final DevelopmentComponent component =
            this.developmentComponentFactory.get("example.com", "example/development/component");

        for (final ActivityResource resource : resources) {
            assertEquals(this.activity, resource.getActivity());
            assertEquals(component, resource.getDevelopmentComponent());
        }
    }

    @Test
    public void testActivityResourceExctractionWithNonDCResources() {
        final Collection<ActivityResource> resources = this.getActivityResources("NonDCResourceDetails.htm");
        assertNotNull(resources);
        assertEquals(0, resources.size());
    }

    @Test
    public void testThatExtractedRessourcesDCsAreAssignedToCompartment() {
        for (final ActivityResource resource : this.getActivityResources("ResourceList.html")) {
            assertThat(compartment, is(resource.getDevelopmentComponent().getCompartment()));
            assertThat(compartment.getDevelopmentComponents(), hasItems(resource.getDevelopmentComponent()));
        }
    }

    /**
     * @return
     */
    private Collection<ActivityResource> getActivityResources(final String resourceName) {
        final InputStream input = this.getClass().getResourceAsStream(resourceName);

        this.activityResourceParser.parse(input);

        return this.activity.getResources();
    }
}
