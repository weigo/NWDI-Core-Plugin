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
        this.developmentComponentFactory = new DevelopmentComponentFactory();
        this.activity = new Activity("URL", new Principal("name"), "description", new Date());
        this.activityResourceParser = new ActivityResourceParser(this.developmentComponentFactory, this.activity);
    }

    @Test
    public void testActivityResourceExctraction() {
        final Collection<ActivityResource> resources = this.getActivityResources("ResourceDetails.htm");
        assertNotNull(resources);
        final DevelopmentComponent component = this.developmentComponentFactory.get("example.com", "example/development/component");

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

    /**
     * @return
     */
    private Collection<ActivityResource> getActivityResources(final String resourceName) {
        final InputStream input = this.getClass().getResourceAsStream(resourceName);

        this.activityResourceParser.parse(input);

        return this.activity.getResources();
    }
}
