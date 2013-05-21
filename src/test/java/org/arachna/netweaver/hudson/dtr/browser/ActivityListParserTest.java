/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
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
        browser.parse(input);
        final List<Activity> activities = browser.getActivities();
        assertThat(null, is(not(equalTo(activities))));
        assertThat(activities.size(), is(greaterThan(0)));

        final Activity activity = activities.get(0);

        assertThat(ACTIVITY_QUERY_URL, is(equalTo(activity.getActivityUrl())));
        assertThat("Anpassung configArchive", is(equalTo(activity.getComment())));
        assertThat("developer00", is(equalTo(activity.getPrincipal().getUser())));

        final SimpleDateFormat format = new SimpleDateFormat(ActivityListParser.ACTIVITY_DATE_FORMAT);

        try {
            assertThat(format.parse("17.05.2010 14:40:12 GMT"), is(equalTo(activity.getCheckInTime())));
        }
        catch (final ParseException e) {
            fail(e.getMessage());
        }
    }
}
