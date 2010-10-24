/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.scm.SCMRevisionState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.arachna.netweaver.hudson.dtr.browser.Activity;

/**
 * A {@link SCMRevisionState} for {@link NWDIScm}.
 * 
 * @author Dirk Weigenand
 */
public final class NWDIRevisionState extends SCMRevisionState implements Serializable {
    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 3469572897964624203L;

    /**
     * A collection of activities checked in since the last build.
     */
    private final Collection<Activity> activities = new ArrayList<Activity>();

    /**
     * Create an instance of <code></code> with the given collection of
     * activities.
     * 
     * @param activities
     *            the activities checked in since the last build.
     */
    public NWDIRevisionState(final Collection<Activity> activities) {
        if (activities != null) {
            this.activities.addAll(activities);
        }
    }

    /**
     * Return the collection of activities that were checked in since the last
     * build.
     * 
     * @return the activities that were checked in since the last build.
     */
    public Collection<Activity> getActivities() {
        return Collections.unmodifiableCollection(activities);
    }
}
