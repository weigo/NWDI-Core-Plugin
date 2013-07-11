/**
 * 
 */
package org.arachna.velocity;

import java.util.logging.Level;
import java.util.logging.Logger;

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
    private final Logger logger;

    public VelocityLogChute(final Logger logger) {
        this.logger = logger;
    }

    /**
     * This init() will be invoked once by the LogManager to give you the current RuntimeServices intance
     */
    public void init(final RuntimeServices rsvc) {
        // do nothing
    }

    /**
     * This is the method that you implement for Velocity to call with log messages.
     */
    public void log(final int level, final String message) {
        logger.log(getLevel(level), message);
    }

    /**
     * This is the method that you implement for Velocity to call with log messages.
     */
    public void log(final int level, final String message, final Throwable t) {
        logger.log(getLevel(level), message, t);
    }

    /**
     * This is the method that you implement for Velocity to check whether a specified log level is enabled.
     */
    public boolean isLevelEnabled(final int level) {
        /* do something useful */
        return true;
    }

    private Level getLevel(final int level) {
        Level result = null;

        switch (level) {
        case LogChute.DEBUG_ID:
            result = Level.FINER;
            break;
        case LogChute.TRACE_ID:
            result = Level.FINE;
            break;
        case LogChute.INFO_ID:
            result = Level.INFO;
            break;
        case LogChute.WARN_ID:
            result = Level.WARNING;
            break;
        case LogChute.ERROR_ID:
            result = Level.SEVERE;
            break;

        default:
            result = Level.ALL;
            break;
        }

        return result;
    }
}
