/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.tasks.Builder;

import org.apache.tools.ant.types.FileSet;
import org.arachna.ant.AntHelper;

/**
 * Base class {@link Builder}s using an Ant task.
 *
 * @author Dirk Weigenand
 */
public abstract class AntTaskBuilder extends Builder {
    private transient AntHelper antHelper;

    /**
     * Helper object to inject for setting up an Ant tasks {@link FileSet}s and
     * class path.
     *
     * @param antHelper
     *            Helper object to inject for setting up an Ant tasks
     *            {@link FileSet}s and class path.
     */
    public final void setAntHelper(AntHelper antHelper) {
        this.antHelper = antHelper;
    }

    /**
     * Returns the
     *
     * @return
     */
    protected final AntHelper getAntHelper() {
        return this.antHelper;
    }
}
