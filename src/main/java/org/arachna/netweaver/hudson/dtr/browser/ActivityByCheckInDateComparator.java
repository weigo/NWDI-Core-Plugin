/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.util.Comparator;

/**
 * Comparator for {@link Activity} by check in date.
 * 
 * @author Dirk Weigenand
 */
public final class ActivityByCheckInDateComparator implements Comparator<Activity> {
    /**
     * {@inheritDoc}
     */
    public int compare(final Activity activity1, final Activity activity2) {
        return activity1.getCheckinTime().compareTo(activity2.getCheckinTime());
    }
}
