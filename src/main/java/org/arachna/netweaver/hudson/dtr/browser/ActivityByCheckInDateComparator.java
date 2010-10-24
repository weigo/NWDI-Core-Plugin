/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.util.Comparator;

/**
 * Comparator for {@link Activity} by checkin date.
 * 
 * @author g526521
 */
public final class ActivityByCheckInDateComparator implements Comparator<Activity> {
    /*
     * (non-Javadoc)
     * 
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(final Activity activity1, final Activity activity2) {
        return activity1.getCheckinTime().compareTo(activity2.getCheckinTime());
    }
}
