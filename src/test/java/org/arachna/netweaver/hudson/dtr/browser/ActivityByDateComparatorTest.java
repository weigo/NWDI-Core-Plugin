/**
 * 
 */
package org.arachna.netweaver.hudson.dtr.browser;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.util.Calendar;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand (G526521)
 * 
 */
public class ActivityByDateComparatorTest {
    /**
     * Activity one used in test.
     */
    private Activity a1;

    /**
     * Activity two used in test.
     */
    private Activity a2;

    /**
     * comparator to be tested.
     */
    private ActivityByDateComparator comparator;

    private Calendar cal;

    private final Principal p = new Principal("user");

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        cal = Calendar.getInstance();
        a1 = new Activity("", p, "", cal.getTime());
        a2 = a1;
        comparator = new ActivityByDateComparator();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        a1 = null;
        a2 = null;
        comparator = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.dtr.browser.ActivityByDateComparator#compare(org.arachna.netweaver.hudson.dtr.browser.Activity, org.arachna.netweaver.hudson.dtr.browser.Activity)}
     * .
     */
    @Test
    public final void testCompareA1WithA1ReturnsZero() {
        assertThat(comparator.compare(a1, a2), equalTo(0));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.dtr.browser.ActivityByDateComparator#compare(org.arachna.netweaver.hudson.dtr.browser.Activity, org.arachna.netweaver.hudson.dtr.browser.Activity)}
     * .
     */
    @Test
    public final void testCompareLaterActivityWithPriorActivityReturnsOne() {
        cal.add(Calendar.DAY_OF_MONTH, 1);
        a2 = new Activity("", p, "", cal.getTime());
        assertThat(comparator.compare(a2, a1), equalTo(1));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.hudson.dtr.browser.ActivityByDateComparator#compare(org.arachna.netweaver.hudson.dtr.browser.Activity, org.arachna.netweaver.hudson.dtr.browser.Activity)}
     * .
     */
    @Test
    public final void testComparePriorActivityWithLaterActivityReturnsMinusOne() {
        cal.add(Calendar.DAY_OF_MONTH, 1);
        a2 = new Activity("", p, "", cal.getTime());
        assertThat(comparator.compare(a1, a2), equalTo(-1));
    }
}
