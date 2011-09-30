/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.tasks.Builder;

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
