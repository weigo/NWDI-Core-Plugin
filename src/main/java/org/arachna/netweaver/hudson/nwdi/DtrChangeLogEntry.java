/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Util;
import hudson.model.User;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.ActivityResource;

/**
 * An entry in a changelog for DTR activities.
 * 
 * @author G526521
 */
public final class DtrChangeLogEntry extends Entry {
    static final String DATE_FORMAT_SPEC = "yyyyMMdd HH:mm:ss Z";

    /**
     * affected resources of the activity.
     */
    private final Set<String> affectedPaths = new HashSet<String>();

    /**
     * the user responsible for the activity.
     */
    private String user;

    /**
     * the commit message.
     */
    private String msg = "";

    /**
     * Id of the activity.
     */
    private String activityId;

    /**
     * the time the activity was checked in.
     */
    private Date checkInTime;

    /**
     * Create an instance of a change log entry from the given activity.
     * 
     * @param activity
     *            activity to use creating the change log.
     */
    public DtrChangeLogEntry(final Activity activity) {
        this(activity.getPrincipal().getUser(), activity.getDescription(), activity.getActivityId(), activity
                .getCheckinTime());

        for (final ActivityResource resource : activity.getResources()) {
            this.affectedPaths.add(resource.getPath());
        }
    }

    /**
     * Create an instance of a change log entry from the given arguments.
     * 
     * @param principal
     *            user that created the activity.
     * @param msg
     *            checkin message.
     * @param id
     *            Id of the activity.
     * @param checkInTime
     *            time the activity was checked in.
     */
    public DtrChangeLogEntry(final String principal, final String msg, final String id, final Date checkInTime) {
        this.user = principal;
        this.activityId = id;
        this.checkInTime = checkInTime;
        this.setMsg(msg);
    }

    public DtrChangeLogEntry() {
    }

    @Override
    public Collection<String> getAffectedPaths() {
        return affectedPaths;
    }

    @Override
    public User getAuthor() {
        return User.get(this.user, true);
    }

    @Override
    public String getMsg() {
        return Util.xmlEscape(this.msg);
    }

    public String getVersion() {
        return this.activityId;
    }

    public Date getCheckInTime() {
        return this.checkInTime;
    }

    /**
     * @param msg
     *            the msg to set
     */
    void setMsg(final String msg) {
        this.msg = msg == null ? "" : msg;
    }

    /**
     * @param activityId
     *            the activityId to set
     */
    void setVersion(final String activityId) {
        this.activityId = activityId;
    }

    /**
     * @param checkInTime
     *            the checkInTime to set
     * @throws ParseException
     */
    void setCheckInTime(final String checkInTime) {
        final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_SPEC);

        try {
            this.checkInTime = format.parse(checkInTime);
        }
        catch (final ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void addAffectedPath(final String path) {
        this.affectedPaths.add(path);
    }

    void setUser(final String user) {
        this.user = user;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    @Override
    protected void setParent(final ChangeLogSet parent) {
        super.setParent(parent);
    }
}
