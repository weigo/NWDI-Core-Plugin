/**
 * 
 */
package org.arachna.velocity;

import java.io.PrintStream;
import java.util.Properties;

import org.apache.velocity.app.VelocityEngine;

/**
 * Helper class to encapsulate common code relevant to use of the Velocity
 * template engine.
 * 
 * @author Dirk Weigenand
 */
public final class VelocityHelper {
    /**
     * the velocity engine to use.
     */
    private final VelocityEngine engine;

    /**
     * Create a new instance of this <code>VelocityHelper</code> using the given
     * logger for logging of velocity related messages.
     * 
     * @param logger
     *            use for logging of messages
     */
    public VelocityHelper(final PrintStream logger) {
        try {
            this.engine = new VelocityEngine();
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream(
                "/org/arachna/netweaver/hudson/nwdi/velocity.properties"));
            engine.setProperty(VelocityEngine.RUNTIME_LOG_LOGSYSTEM, new VelocityLogChute(logger));
            engine.init(properties);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the <code>VelocityEngine</code> embedded in this helper class.
     * 
     * @return the <code>VelocityEngine</code> embedded in this helper class.
     */
    public VelocityEngine getVelocityEngine() {
        return this.engine;
    }
}
