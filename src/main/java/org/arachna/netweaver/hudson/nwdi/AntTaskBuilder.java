/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.tasks.Builder;
import hudson.tasks.Ant;
import hudson.tasks.Ant.AntInstallation;
import hudson.tools.ToolInstallation;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.ant.AntHelper;
import org.arachna.velocity.VelocityHelper;

/**
 * Base class {@link Builder}s using an Ant task.
 * 
 * @author Dirk Weigenand
 */
public abstract class AntTaskBuilder extends Builder {
    /**
     * AntHelper to use for build file generation.
     */
    private transient AntHelper antHelper;

    /**
     * Execute an Ant build using the ant-plugin.
     * 
     * @param build
     *            the build the ant build is executed for
     * @param launcher
     *            launcher to launch the ant subprocess with
     * @param listener
     *            the build listener
     * @param defaultTarget
     *            the default target to call in the ant build
     * @param buildFileName
     *            the name of the build file that shall be executed
     * @param antOpts
     *            options for the ant process (ANT_OPTS environment variable)
     * @return returns <code>true</code> when the ant build returned successfully, <code>false</code> otherwise
     * @throws InterruptedException
     *             forward exception so callers can cancel build.
     */
    protected final boolean execute(final AbstractBuild<?, ?> build, final Launcher launcher, final BuildListener listener,
        final String defaultTarget, final String buildFileName, final String antOpts) throws InterruptedException {
        boolean result = false;
        final AntInstallation.DescriptorImpl descriptor = ToolInstallation.all().get(AntInstallation.DescriptorImpl.class);
        final AntInstallation[] installations = descriptor.getInstallations();

        if (installations != null && installations.length > 0) {
            final Ant ant = new Ant(defaultTarget, installations[0].getName(), antOpts, buildFileName, getAntProperties());

            try {
                result = ant.perform(build, launcher, listener);
            }
            catch (final IOException e) {
                e.printStackTrace(listener.getLogger());
            }
        }
        else {
            listener.getLogger().println(Messages.AntTaksBuilder_missing_ant_installation());
        }

        return result;
    }

    /**
     * Helper object to inject for setting up an Ant tasks.
     * 
     * @param antHelper
     *            Helper object to inject for setting up an Ant tasks.
     */
    public final void setAntHelper(final AntHelper antHelper) {
        this.antHelper = antHelper;
    }

    /**
     * Returns the <code>AntHelper</code> object to use for this build step.
     * 
     * @return <code>AntHelper</code> object to use for this build step
     */
    protected final AntHelper getAntHelper() {
        return antHelper;
    }

    /**
     * Create a new VelocityEngine.
     * 
     * @return the new VelocityEngine.
     */
    protected VelocityEngine getVelocityEngine() {
        return new VelocityHelper().getVelocityEngine();
    }

    /**
     * Get the {@link Reader} for the velocity template.
     * 
     * @param pathToResourceInClassPath
     *            path to velocity template in classpath.
     * @return the reader for the velocity template.
     */
    protected final Reader getTemplateReader(final String pathToResourceInClassPath) {
        return new InputStreamReader(this.getClass().getResourceAsStream(pathToResourceInClassPath), Charset.forName("UTF-8"));
    }

    /**
     * Get the properties to use when calling ant.
     * 
     * @return the properties to use when calling ant.
     */
    protected abstract String getAntProperties();
}
