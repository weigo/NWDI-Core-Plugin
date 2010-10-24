/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.junit.Test;

/**
 * Unit test for {@link ActivityListParser}.
 * 
 * @author Dirk Weigenand
 */
public class ActivityListParserTest {
    /**
     * URL for activity query.
     */
    private static final String ACTIVITY_QUERY_URL = "/dtr/system-tools/reports/ResourceDetails?technical=false&"
            + "path=/act/act_w_Example_example_2e_com_EXAMPLE_2d_SC1_dev_inactive_u_"
            + "developer00_t_2010_05_17_14_29_57_GMT_50220574-193f-45a9-82bc-eb730df05ccc";

    /**
     * Test method for {@link org.arachna.netweaver.hudson.dtr.browser.ActivityListParser#parse(java.io.InputStream)} .
     */
    @Test
    public final void testExctractActivities() {
        final ActivityListParser browser = new ActivityListParser();
        final InputStream input = this.getClass().getResourceAsStream("ExampleTrackActivityQuery.html");
        final List<Activity> activities = browser.parse(input);
        assertNotNull(activities);
        assertTrue(activities.size() > 0);
        final Activity activity = activities.get(0);
        assertEquals(ACTIVITY_QUERY_URL, activity.getActivityUrl());
        assertEquals("Anpassung configArchive", activity.getDescription());
        assertEquals("developer00", activity.getPrincipal().getUser());
        final SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z");
        try {
            assertEquals(format.parse("17.05.2010 14:40:12 GMT"), activity.getCheckinTime());
        }
        catch (final ParseException e) {
            fail(e.getMessage());
        }
    }
}
