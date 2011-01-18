/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

import java.util.Date;

import org.arachna.netweaver.dc.types.DevelopmentComponent;

/**
 * A resource attached to an activity.
 * 
 * @author Dirk Weigenand
 */
public class ActivityResource {
    /**
     * Path to resource.
     */
    private final String path;

    /**
     * Activity this resource is associated with.
     */
    private final Activity activity;

    /**
     * Development component this resource belongs to.
     */
    private final DevelopmentComponent developmentComponent;

    /**
     * date the resource was created at.
     */
    private Date creationDate;

    /**
     * date this resource was last modified at.
     */
    private Date lastModified;

    /**
     * signifies whether this resources last modification was a deletion.
     */
    private Boolean deleted;

    /**
     * sequence in DTR (version???).
     */
    private Integer sequenceNumber;

    /**
     * Create an instance of an <code>ActivityResource</code>.
     * 
     * @param activity
     *            {@link Activity} this resource is associated with.
     * @param developmentComponent
     *            {@link org.arachna.netweaver.dc.types.DevelopmentComponent}
     *            this resource belongs to.
     * @param path
     *            path of resource in the containing development component.
     */
    public ActivityResource(final Activity activity, final DevelopmentComponent developmentComponent, final String path) {
        super();
        this.activity = activity;
        this.developmentComponent = developmentComponent;
        this.path = path;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the activity
     */
    public Activity getActivity() {
        return activity;
    }

    /**
     * @return the developmentComponent
     */
    public DevelopmentComponent getDevelopmentComponent() {
        return developmentComponent;
    }

    /**
     * @return the creationDate
     */
    public Date getCreationDate() {
        return creationDate;
    }

    /**
     * @param creationDate
     *            the creationDate to set
     */
    void setCreationDate(final Date creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * @return the lastModified
     */
    public Date getLastModified() {
        return lastModified;
    }

    /**
     * @param lastModified
     *            the lastModified to set
     */
    void setLastModified(final Date lastModified) {
        this.lastModified = lastModified;
    }

    /**
     * @return the deleted
     */
    public Boolean isDeleted() {
        return this.deleted;
    }

    /**
     * @param deleted
     *            the deleted to set
     */
    void setDeleted(final Boolean deleted) {
        this.deleted = deleted;
    }

    /**
     * @return the sequenceNumber
     */
    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    /**
     * @param sequenceNumber
     *            the sequenceNumber to set
     */
    void setSequenceNumber(final Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }
}
