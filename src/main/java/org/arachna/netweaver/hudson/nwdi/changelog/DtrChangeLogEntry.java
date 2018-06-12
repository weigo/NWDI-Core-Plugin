/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.changelog;

import hudson.Util;
import hudson.model.User;
import hudson.scm.EditType;
import hudson.scm.ChangeLogSet.AffectedFile;
import hudson.scm.ChangeLogSet.Entry;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.arachna.netweaver.hudson.dtr.browser.ActivityResource;

/**
 * An entry in a changelog for DTR activities.
 * 
 * @author Dirk Weigenand
 */
public final class DtrChangeLogEntry extends Entry {
    /**
     * date format specification for check in times.
     */
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
    private String activityUrl;

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
        this(activity.getPrincipal().getUser(), activity.getComment(), activity.getActivityUrl(), activity.getCheckInTime());
        setDescription(activity.getDescription());

        for (final ActivityResource resource : activity.getResources()) {
            if (resource != null) {
                createAndAddItem(resource);
            }
            else {
                Logger.getLogger(getClass().getName()).fine(String.format("Null resource for activity %s!", activity.getActivityUrl()));
            }
        }
    }

    /**
     * Create an instance of a change log entry from the given arguments.
     * 
     * @param principal
     *            user that created the activity.
     * @param msg
     *            check-in message.
     * @param activityUrl
     *            DTR-URL to browse the activity.
     * @param checkInTime
     *            time the activity was checked in.
     */
    public DtrChangeLogEntry(final String principal, final String msg, final String activityUrl, final Date checkInTime) {
        user = principal;
        this.activityUrl = activityUrl;
        this.checkInTime = new Date(checkInTime.getTime());
        setMsg(msg);
    }

    /**
     * default constructor for xstream.
     */
    public DtrChangeLogEntry() {
    }

    /**
     * Add {@link Item}.
     * 
     * @param item
     *            item to add to this changelog entry.
     */
    public void add(final Item item) {
        item.setParent(this);
        items.add(item);
    }

    /**
     * Add the given {@link ActivityResource} as an {@link Item} to this changelog entry.
     * 
     * @param resource
     *            <code>ActivityResource</code> to add to change log.
     */
    private void createAndAddItem(final ActivityResource resource) {
        Action action = Action.EDIT;

        if (resource.isDeleted()) {
            action = Action.DELETE;
        }
        else if (Integer.valueOf(1).equals(resource.getSequenceNumber())) {
            action = Action.ADD;
        }

        final DevelopmentComponent dc = resource.getDevelopmentComponent();
        add(new Item(String.format("%s/%s/comp_/%s", dc.getVendor(), dc.getName(), resource.getPath()), action));
    }

    /**
     * Get paths (to resources) affected by the recent activities depicted by this change log.
     * 
     * @return paths (to resources) affected by the recent activities depicted by this change log.
     */
    @Override
    public Collection<String> getAffectedPaths() {
        final Set<String> affectedPaths = new HashSet<String>();

        for (final Item item : items) {
            affectedPaths.add(item.getPath());
        }

        return affectedPaths;
    }

    @Override
    public Collection<? extends AffectedFile> getAffectedFiles() {
        return getItems();
    }

    /**
     * Returns the {@link Item}s associated with this entry.
     * 
     * @return the {@link Item}s associated with this entry.
     */
    public Collection<Item> getItems() {
        return items;
    }

    /**
     * Returns the author of this entry.
     * 
     * @return the author of this entry.
     */
    @Override
    public User getAuthor() {
        return User.get(user, true);
    }

    /**
     * Returns the message associated with this entry.
     * 
     * @return the commit message of this entry.
     */
    @Override
    public String getMsg() {
        return Util.xmlEscape(msg);
    }

    /**
     * Returns the time this activity was checked in.
     * 
     * @return the time this activity was checked in.
     */
    public Date getCheckInTime() {
        return new Date(checkInTime.getTime());
    }

    /**
     * @param msg
     *            the msg to set
     */
    public void setMsg(final String msg) {
        this.msg = msg == null ? "" : msg;
    }

    /**
     * @param checkInTime
     *            the checkInTime to set
     * @throws IllegalArgumentException when the checkInTime could not be parsed
     */
    public void setCheckInTime(final String checkInTime) {
        final SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT_SPEC);

        try {
            this.checkInTime = format.parse(checkInTime);
        }
        catch (final ParseException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public void setUser(final String user) {
        this.user = user;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * Returns the URL that can be used to display information about this activity.
     * 
     * @return the URL that can be used to display information about this activity.
     */
    public String getActivityUrl() {
        return activityUrl;
    }

    /**
     * @param activityUrl
     *            the activityUrl to set
     */
    public void setActivityUrl(final String activityUrl) {
        this.activityUrl = activityUrl;
    }

    /**
     * Returns the long description of this DtrChangeLogEntry ({@link Activity}.
     * 
     * @return the long description of this DtrChangeLogEntry
     */
    public String getDescription() {
        return Util.xmlEscape(description);
    }

    /**
     * Sets the long description of this DtrChangeLogEntry ({@link Activity}.
     * 
     * @param description
     *            the long description of this DtrChangeLogEntry
     */
    public void setDescription(final String description) {
        this.description = description == null ? "" : description;
    }

    /**
     * Add this change log entry to the given set of change logs.
     * 
     * @param parent
     *            change log to use as parent.
     */
    void addTo(final DtrChangeLogSet parent) {
        super.setParent(parent);
    }

    /**
     * An <code>Item</code> represents a resource associated with an activity an is used to visualize it.
     * 
     * @author Dirk Weigenand
     */
    public static final class Item implements AffectedFile {
        /**
         * Path to resource in the affected DC.
         */
        private final String path;

        /**
         * Action (added, edited, removed).
         */
        private final Action action;

        /**
         * the change log entry this item belongs to.
         */
        private DtrChangeLogEntry parent;

        /**
         * Creates an item with the given path and action.
         * 
         * @param path
         *            Path to resource in the affected DC.
         * @param action
         *            Action (added, edited, removed).
         */
        Item(final String path, final Action action) {
            this.path = path;
            this.action = action;
        }

        /**
         * Set the change log entry this item belongs to.
         * 
         * @param parent
         *            the change log entry this item belongs to.
         */
        void setParent(final DtrChangeLogEntry parent) {
            this.parent = parent;
        }

        /**
         * Returns the path to resource in the affected DC.
         * 
         * @return the path to resource in the affected DC.
         */
        public String getPath() {
            return path;
        }

        /**
         * Returns the action associated with this item.
         * 
         * @return the action associated with this item.
         */
        public Action getAction() {
            return action;
        }

        /**
         * Returns the change log entry this item belongs to.
         * 
         * @return the change log entry this item belongs to.
         */
        public DtrChangeLogEntry getParent() {
            return parent;
        }

        /**
         * Returns the {@link EditType} matching this items action.
         * 
         * @return the {@link EditType} matching this items action.
         */
        public EditType getEditType() {
            EditType editType = EditType.EDIT;

            if (action.equals(Action.DELETE)) {
                editType = EditType.DELETE;
            }
            else if (action.equals(Action.ADD)) {
                editType = EditType.ADD;
            }

            return editType;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + (action == null ? 0 : action.hashCode());
            result = prime * result + (parent == null ? 0 : parent.hashCode());
            result = prime * result + (path == null ? 0 : path.hashCode());
            return result;
        }

        /**
         * {@inheritDoc}
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
            final Item other = (Item)obj;
            if (action != other.action) {
                return false;
            }

            if (path == null) {
                if (other.path != null) {
                    return false;
                }
            }
            else if (!path.equals(other.path)) {
                return false;
            }

            return true;
        }

    }

    /**
     * Actions on resources associated with activities.
     * 
     * @author Dirk Weigenand
     * 
     */
    enum Action {
        /**
         * action denoting an add operation.
         */
        ADD("add"),
        /**
         * action denoting a delete operation.
         */
        DELETE("delete"),
        /**
         * action denoting an edit operation.
         */
        EDIT("edit");

        /**
         * operation denoted by an action.
         */
        private String name;

        /**
         * Create an instance of an action using the given name.
         * 
         * @param name
         *            operation denoted by this action.
         */
        Action(final String name) {
            this.name = name;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString() {
            return name;
        }
    }
}
