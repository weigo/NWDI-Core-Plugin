/**
 * 
 */
package org.arachna.velocity;

import java.io.PrintStream;

import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

/**
 * @author Dirk Weigenand
 * 
 */
public class VelocityLogChute implements LogChute {
    /**
     * Logger to use.
     */
    private final PrintStream logger;

    public VelocityLogChute(final PrintStream logger) {
        this.logger = logger;
    }

    /**
     * This init() will be invoked once by the LogManager to give you the
     * current RuntimeServices intance
     */
    public void init(RuntimeServices rsvc) {
        // do nothing
    }

    /**
     * This is the method that you implement for Velocity to call with log
     * messages.
     */
    public void log(int level, String message) {
        this.logger.append(message);
    }

    /**
     * This is the method that you implement for Velocity to call with log
     * messages.
     */
    public void log(int level, String message, Throwable t) {
        this.logger.append(message);
        t.printStackTrace(logger);
    }

    /**
     * This is the method that you implement for Velocity to check whether a
     * specified log level is enabled.
     */
    public boolean isLevelEnabled(int level) {
        /* do something useful */
        return true;
    }
}
