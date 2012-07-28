/**
 * 
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.util.Comparator;

/**
 * Compare two {@link Activty} objects by their respective check-in times.
 * 
 * @author Dirk Weigenand
 */
public final class ActivityByDateComparator implements Comparator<Activity> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(Activity a1, Activity a2) {
        return a1.getCheckinTime().compareTo(a2.getCheckinTime());
    }
}
