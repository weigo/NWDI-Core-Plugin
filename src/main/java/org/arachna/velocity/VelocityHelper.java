/**
 * 
 */
package org.arachna.velocity;

import java.util.Properties;
import java.util.logging.Logger;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

/**
 * Helper class to encapsulate common code relevant to use of the Velocity template engine.
 * 
 * @author Dirk Weigenand
 */
public final class VelocityHelper {
    /**
     * the velocity engine to use.
     */
    private final VelocityEngine engine;

    /**
     * Create a new instance of this <code>VelocityHelper</code> using the given logger for logging of velocity related messages.
     */
    public VelocityHelper() {
        try {
            engine = new VelocityEngine();
            final Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/org/arachna/netweaver/hudson/nwdi/velocity.properties"));
            engine.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
            engine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
            engine.init(properties);
        }
        // CHECKSTYLE:OFF
        catch (final Exception e) {
            // CHECKSTYLE:ON
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the <code>VelocityEngine</code> embedded in this helper class.
     * 
     * @return the <code>VelocityEngine</code> embedded in this helper class.
     */
    public VelocityEngine getVelocityEngine() {
        return engine;
    }
}
