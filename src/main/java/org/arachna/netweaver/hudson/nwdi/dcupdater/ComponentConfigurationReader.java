/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.util.Set;

import org.arachna.netweaver.dc.types.PublicPartReference;

/**
 * Interface for readers that provide additional information about a development
 * component apart from which can be read from the <code>.dcdef</code>
 * configuration file.
 * 
 * @author Dirk Weigenand
 */
public interface ComponentConfigurationReader {
    /**
     * Read the public part references from the configuration for this type of
     * development component.
     * 
     * @return the public part references from the configuration for this type
     *         of development component.
     */
    Set<PublicPartReference> read();
}
