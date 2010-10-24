/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents an activity in the DTR.
 * 
 * @author G526521
 */
public final class Activity {
    /**
     * relative URL to DTR where the contents of this activity can be queried.
     */
    private final String activityUrl;

    /**
     * the principal that created this activity.
     */
    private final Principal principal;

    /**
     * short description of activity as given by princiapl that created this
     * activity.
     */
    private final String description;

    /**
     * date and time of check in of this activity.
     */
    private final Date checkinTime;

    /**
     * Resources associated with this activity.
     */
    private final Set<ActivityResource> resources = new HashSet<ActivityResource>();

    /**
     * Create an instance of an <code>Activity</code> using the principal that
     * created it, its description and checkin date. Also contains the relative
     * URL where the content of the activity can be browsed.
     * 
     * @param activityUrl
     *            relative URL where the content of the activity can be browsed.
     * @param principal
     *            user that created the activity.
     * @param description
     *            the short description of the activity as was given by the user
     *            creating it.
     * @param checkinTime
     *            time the activity was checked into the DTR.
     */
    Activity(final String activityUrl, final Principal principal, final String description, final Date checkinTime) {
        this.activityUrl = activityUrl;
        this.principal = principal;
        this.description = description;
        this.checkinTime = this.cloneDate(checkinTime);
    }

    /**
     * @param checkinTime
     * @return
     */
    private Date cloneDate(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());

        return calendar.getTime();
    }

    /**
     * @return the activityUrl
     */
    public String getActivityUrl() {
        return this.activityUrl;
    }

    /**
     * @return the principal
     */
    public Principal getPrincipal() {
        return this.principal;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * @return the checkinTime
     */
    public Date getCheckinTime() {
        return this.cloneDate(this.checkinTime);
    }

    /**
     * Get the Id of this activity.
     * 
     * @return Id of this activity
     */
    public String getActivityId() {
        return this.getActivityUrl().substring(this.getActivityUrl().lastIndexOf('/'));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Activity [checkinTime=" + this.checkinTime + ", principal=" + this.principal + ",\ndescription="
                + this.description + ",\nactivityUrl=" + this.activityUrl + "]";
    }

    /**
     * Add the given resource to this activity's resources. If the given
     * resource is <code>null</code> it is ignored.
     * 
     * @param resource
     *            resource to add to this activity's resources.
     */
    void add(final ActivityResource resource) {
        if (resource != null) {
            this.resources.add(resource);
        }
    }

    /**
     * Return an unmodifiable collection of this activity's resources.
     * 
     * @return an unmodifiable collection of this activity's resources.
     */
    public Collection<ActivityResource> getResources() {
        return Collections.unmodifiableCollection(this.resources);
    }
}
