/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Represents an activity in the DTR.
 * 
 * @author Dirk Weigenand
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
     * short description of activity as given by principal that created this activity.
     */
    private final String comment;

    /**
     * long description of activity as given by principal that created this activity.
     */
    private String description;

    /**
     * date and time of check in of this activity.
     */
    private final Date checkinTime;

    /**
     * Resources associated with this activity.
     */
    private final Set<ActivityResource> resources = new LinkedHashSet<ActivityResource>();

    /**
     * Map URL param names to their respective values.
     */
    private final Map<String, String> activityUrlParams = new LinkedHashMap<String, String>();

    /**
     * Create an instance of an <code>Activity</code> using the principal that created it, its description and checkin date. Also contains
     * the relative URL where the content of the activity can be browsed.
     * 
     * @param activityUrl
     *            relative URL where the content of the activity can be browsed.
     * @param principal
     *            user that created the activity.
     * @param comment
     *            the short description of the activity as was given by the user creating it.
     * @param checkinTime
     *            time the activity was checked into the DTR.
     */
    Activity(final String activityUrl, final Principal principal, final String comment, final Date checkinTime) {
        this.activityUrl = activityUrl;
        this.principal = principal;
        this.comment = comment;
        this.checkinTime = cloneDate(checkinTime);

        parseActivityUrlParameters();
    }

    /**
     * Parse parameters of activity URL and store them into map for easier use later.
     */
    private void parseActivityUrlParameters() {
        final int paramSpecStartIndex = StringUtils.isBlank(activityUrl) ? -1 : activityUrl.indexOf('?') + 1;

        if (paramSpecStartIndex > -1) {
            final String paramSpecs = activityUrl.substring(paramSpecStartIndex);
            for (final String parameterSpec : paramSpecs.split("&")) {
                final String[] parameter = parameterSpec.split("=");
                activityUrlParams.put(parameter[0], parameter[1]);
            }
        }
    }

    /**
     * Create a copy of the given date object.
     * 
     * @param date
     *            date object to clone.
     * @return cloned date object.
     */
    private Date cloneDate(final Date date) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());

        return calendar.getTime();
    }

    /**
     * Set the long description of this activity.
     * 
     * @param description
     *            long description of this activity.
     */
    void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the activityUrl
     */
    public String getActivityUrl() {
        return activityUrl;
    }

    /**
     * @return the principal
     */
    public Principal getPrincipal() {
        return principal;
    }

    /**
     * Returns the short description of this activity.
     * 
     * @return the short description of this activity.
     */
    public String getComment() {
        return comment;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the checkinTime
     */
    public Date getCheckInTime() {
        return cloneDate(checkinTime);
    }

    /**
     * Returns the path part of the activity url (i.e. the string after '&amp;path=').
     * 
     * @return the path part of the activity url
     */
    public String getActivityPath() {
        return activityUrlParams.get("path");
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Activity [checkinTime=" + checkinTime + ", principal=" + principal + ",\ndescription=" + description + ",\nactivityUrl="
            + activityUrl + "]";
    }

    /**
     * Add the given resource to this activity's resources. If the given resource is <code>null</code> it is ignored.
     * 
     * @param resource
     *            resource to add to this activity's resources.
     */
    void add(final ActivityResource resource) {
        if (resource != null) {
            resources.add(resource);
        }
    }

    /**
     * Return an unmodifiable collection of this activity's resources.
     * 
     * @return an unmodifiable collection of this activity's resources.
     */
    public Collection<ActivityResource> getResources() {
        return Collections.unmodifiableCollection(resources);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return getActivityPath().hashCode();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }

        final Activity other = (Activity)obj;

        return hashCode() == other.hashCode();
    }
}
