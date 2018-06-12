/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.scm.SCMRevisionState;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import org.arachna.netweaver.hudson.dtr.browser.Activity;

/**
 * A {@link SCMRevisionState} for {@link NWDIScm}.
 * 
 * @author Dirk Weigenand
 */
public final class NWDIRevisionState extends SCMRevisionState implements Serializable {
    /**
     * Use this as start state when no revision state could be determined yet.
     */
    static final NWDIRevisionState START_STATE = new NWDIRevisionState(new Date(0));

    /**
     * UID for serialization.
     */
    private static final long serialVersionUID = 3469572897964624203L;

    /**
     * Date and time this instance of <code>NWDIRevisionState</code> was
     * created.
     */
    private final Date creationDate;

    /**
     * Create an instance of <code>NWDIRevisionState</code> with the given
     * collection of activities.
     * 
     * @param activities
     *            the activities checked in since the last build.
     */
    public NWDIRevisionState(final Collection<Activity> activities) {
        this(Calendar.getInstance().getTime());
    }

    /**
     * Create an instance of <code>NWDIRevisionState</code> with the current
     * date and time.
     */
    public NWDIRevisionState() {
        this(Calendar.getInstance().getTime());
    }

    /**
     * Create state with the given date.
     * 
     * @param creationDate
     *            the date this state was created.
     */
    private NWDIRevisionState(final Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Returns the date &amp; time this instance of <code>NWDIRevisionState</code>
     * was created.
     * 
     * @return date &amp; time this instance of <code>NWDIRevisionState</code> was
     *         created.
     */
    public Date getCreationDate() {
        return new Date(creationDate.getTime());
    }
}
