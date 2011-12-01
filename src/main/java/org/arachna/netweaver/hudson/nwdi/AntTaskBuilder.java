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
import java.io.PrintStream;
import java.io.Reader;
import java.util.Properties;

import org.apache.tools.ant.types.FileSet;
import org.apache.velocity.app.VelocityEngine;
import org.arachna.ant.AntHelper;
import org.arachna.velocity.VelocityLogChute;

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
     * @return returns <code>true</code> when the ant build returned
     *         successfully, <code>false</code> otherwise
     */
    protected final boolean execute(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener,
        String defaultTarget, String buildFileName, String antOpts) {
        boolean result = false;
        AntInstallation.DescriptorImpl descriptor =
            (AntInstallation.DescriptorImpl)ToolInstallation.all().get(AntInstallation.DescriptorImpl.class);
        AntInstallation[] installations = descriptor.getInstallations();

        if (installations != null && installations.length > 0) {
            Ant ant = new Ant(defaultTarget, installations[0].getName(), antOpts, buildFileName, getAntProperties());

            try {
                result = ant.perform(build, launcher, listener);
            }
            catch (InterruptedException e) {
                result = false;
                e.printStackTrace(listener.getLogger());
            }
            catch (IOException e) {
                result = false;
                e.printStackTrace(listener.getLogger());
            }
        }
        else {
            result = false;
        }

        return result;
    }

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
     * Returns the <code>AntHelper</code> object to use for this build step.
     * 
     * @return <code>AntHelper</code> object to use for this build step
     */
    protected final AntHelper getAntHelper() {
        return this.antHelper;
    }

    /**
     * Create a new VelocityEngine using the given Logger.
     * 
     * @param logger
     *            logger to use for VelocityEngine.
     * @return the new VelocityEngine.
     */
    protected VelocityEngine getVelocityEngine(final PrintStream logger) {
        VelocityEngine engine = null;

        try {
            engine = new VelocityEngine();
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream(
                "/org/arachna/netweaver/hudson/nwdi/velocity.properties"));
            engine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, new VelocityLogChute(logger));
            engine.init(properties);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

        return engine;
    }

    /**
     * Get the {@link Reader} for the velocity template.
     * 
     * @return the reader for the velocity template.
     */
    protected final Reader getTemplateReader(String pathToResourceInClassPath) {
        return new InputStreamReader(this.getClass().getResourceAsStream(pathToResourceInClassPath));
    }

    /**
     * Get the properties to use when calling ant.
     * 
     * @return the properties to use when calling ant.
     */
    protected abstract String getAntProperties();
}
