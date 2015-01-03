/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

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
     */
    @Before
    public void setUp() {
        developmentComponentFactory = new DevelopmentComponentFactory();
        activity = new Activity(null, new Principal("name"), "description", new Date());
        activityResourceParser = new ActivityResourceParser(developmentComponentFactory, activity);
    }

    @Test
    public void testActivityResourceExctraction() {
        final Collection<ActivityResource> resources = getActivityResources("ResourceList.html");
        assertNotNull(resources);
        final DevelopmentComponent component =
            developmentComponentFactory.get("example.com", "example/development/component");

        for (final ActivityResource resource : resources) {
            assertEquals(activity, resource.getActivity());
            assertEquals(component, resource.getDevelopmentComponent());
        }
    }

    @Test
    public void testActivityResourceExctractionWithNonDCResources() {
        final Collection<ActivityResource> resources = getActivityResources("NonDCResourceDetails.htm");
        assertNotNull(resources);
        assertEquals(0, resources.size());
    }

    /**
     * @return
     */
    private Collection<ActivityResource> getActivityResources(final String resourceName) {
        final InputStream input = this.getClass().getResourceAsStream(resourceName);

        activityResourceParser.parse(input);

        return activity.getResources();
    }
}
