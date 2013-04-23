/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.util.Calendar;
import java.util.Date;

/**
 * Filter activities by check in date.
 * 
 * @author Dirk Weigenand
 */
final class ActivityCheckinDateFilter implements ActivityFilter {
    /**
     * start date of range the check in dates will be accepted in.
     */
    private final Date from;

    /**
     * Create an instance of a <code>ActivityCheckinDateFilter</code>.
     * 
     * @param from
     *            start date for date range activities will be accepted in.
     */
    ActivityCheckinDateFilter(final Date from) {
        this.from = from == null ? getMinimumDate() : from;
    }

    /**
     * Create an instance of a <code>ActivityCheckinDateFilter</code>. The
     * accepted date range will be initialized to [minimum, maximum] as is
     * representable by the {@link java.util.Date} class.
     */
    ActivityCheckinDateFilter() {
        from = getMinimumDate();
    }

    /**
     * Get the minimum possible date.
     * 
     * @return the minimum possible date.
     */
    private Date getMinimumDate() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        return cal.getTime();
    }

    /**
     * Tests whether the given activity date lies between the from and to date
     * of this filter.
     * 
     * @param activity
     *            the activity to be tested.
     * @return <code>true</code> iff the given activities check in date/time
     *         lies between this filters from and to date/time,
     *         <code>false</code> otherwise.
     */
    public boolean accept(final Activity activity) {
        return !from.after(activity.getCheckinTime());
    }
}
