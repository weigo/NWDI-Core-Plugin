/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Util;
import hudson.model.User;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;
import hudson.scm.EditType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.ActivityResource;

/**
 * An entry in a changelog for DTR activities.
 * 
 * @author Dirk Weigenand
 */
public final class DtrChangeLogEntry extends Entry {
    static final String DATE_FORMAT_SPEC = "yyyyMMdd HH:mm:ss Z";

    /**
     * affected resources of the activity.
     */
    private final List<Item> items = new LinkedList<Item>();

    /**
     * the user responsible for the activity.
     */
    private String user;

    /**
     * the commit message.
     */
    private String msg = "";

    /**
     * long description of activity.
     */
    private String description = "";

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
        this(activity.getPrincipal().getUser(), activity.getComment(), activity.getActivityUrl(), activity
            .getCheckinTime());
        this.setDescription(activity.getDescription());

        for (final ActivityResource resource : activity.getResources()) {
            createAndAddItem(resource);
        }
    }

    /**
     * @param resource
     */
    private void createAndAddItem(final ActivityResource resource) {
        String action = "edit";

        if (resource.isDeleted()) {
            action = "delete";
        }
        else if (Integer.valueOf(1).equals(resource.getSequenceNumber())) {
            action = "add";
        }

        final DevelopmentComponent dc = resource.getDevelopmentComponent();
        add(new Item(String.format("%s/%s/comp_/%s", dc.getVendor(), dc.getName(), resource.getPath()), action));
    }

    /**
     * @param item
     */
    void add(final Item item) {
        item.setParent(this);
        this.items.add(item);
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
        final Set<String> affectedPaths = new HashSet<String>();

        for (final Item item : this.items) {
            affectedPaths.add(item.getPath());
        }

        return affectedPaths;
    }

    public Collection<Item> getItems() {
        return this.items;
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
        // FIXME: Aktivitäts-ID herausfinden
        return this.activityId.substring(this.activityId.lastIndexOf('_') + 1);
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

    void setUser(final String user) {
        this.user = user;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    public String getActivityUrl() {
        return String.format("%s/dtr/system-tools/reports/ResourceDetails?technical=false&path=/act%s", "",
            this.activityId);
    }

    /**
     * Returns the long description of this DtrChangeLogEntry ({@link Activity}.
     * 
     * @return the long description of this DtrChangeLogEntry
     */
    public String getDescription() {
        return Util.xmlEscape(this.description);
    }

    /**
     * Sets the long description of this DtrChangeLogEntry ({@link Activity}.
     * 
     * @param description
     *            the long description of this DtrChangeLogEntry
     */
    void setDescription(final String description) {
        this.description = description == null ? "" : description;
    }

    @Override
    protected void setParent(final ChangeLogSet parent) {
        super.setParent(parent);
    }

    public static final class Item {
        private final String path;
        private final String action;
        private DtrChangeLogEntry parent;

        Item(final String path, final String action) {
            this.path = path;
            this.action = action;
        }

        void setParent(final DtrChangeLogEntry parent) {
            this.parent = parent;
        }

        /**
         * @return the path
         */
        public String getPath() {
            return path;
        }

        /**
         * @return the action
         */
        public String getAction() {
            return action;
        }

        /**
         * @return the parent
         */
        public DtrChangeLogEntry getParent() {
            return parent;
        }

        public EditType getEditType() {
            EditType editType = EditType.EDIT;

            if (this.action.equalsIgnoreCase("delete")) {
                editType = EditType.DELETE;
            }
            else if (action.equalsIgnoreCase("add")) {
                editType = EditType.ADD;
            }

            return editType;
        }
    }
}
