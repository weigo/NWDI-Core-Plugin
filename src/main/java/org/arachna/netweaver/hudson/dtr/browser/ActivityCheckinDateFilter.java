/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.util.Calendar;
import java.util.Date;

/**
 * Filter activities by checkin date.
 * 
 * @author g526521
 */
final class ActivityCheckinDateFilter implements ActivityFilter {
    /**
     * end date of range the checkin dates will be accepted in.
     */
    private final Date to;

    /**
     * start date of range the checkin dates will be accepted in.
     */
    private final Date from;

    /**
     * Create an instance of a <code>ActivityCheckinDateFilter</code>.
     * 
     * @param from
     *            start date for date range activities will be accepted in.
     * @param to
     *            end date for date range activities will be accepted in.
     */
    ActivityCheckinDateFilter(final Date from, final Date to) {
        this.from = from;
        this.to = to;
    }

    /**
     * Create an instance of a <code>ActivityCheckinDateFilter</code>.
     * 
     * The accepted date range will be initialized to [minimum, maximum] as is
     * representable by the {@link java.util.Date} class.
     */
    ActivityCheckinDateFilter() {
        this.from = this.getMinimumDate();
        this.to = this.getMaximumDate();
    }

    private Date getMaximumDate() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Integer.MAX_VALUE);
        cal.set(Calendar.DAY_OF_MONTH, 31);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);

        return cal.getTime();
    }

    /**
     * @return
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
     * Tests whther the given activity
     */
    public boolean accept(final Activity activity) {
        final Date checkInDate = activity.getCheckinTime();

        return (this.from.equals(checkInDate) || this.from.before(activity.getCheckinTime()))
                && (this.to.equals(checkInDate) || this.to.after(checkInDate));
    }
}
