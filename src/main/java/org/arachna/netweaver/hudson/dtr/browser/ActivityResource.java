/**
 *
 */
package org.arachna.netweaver.hudson.dtr.browser;

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
     * Create an instance of an <code>ActivityResource</code>.
     * 
     * @param activity
     *            {@link Activity} this resource is associated with.
     * @param developmentComponent
     *            {@link org.arachna.netweaver.dc.types.DevelopmentComponent} this resource belongs to.
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
}
