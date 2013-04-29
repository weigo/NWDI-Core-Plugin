/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.Reader;

import org.arachna.netweaver.dc.types.DevelopmentComponent;

/**
 * Interface for readers that provide additional information about a development
 * component apart from which can be read from the <code>.dcdef</code>
 * configuration file.
 * 
 * @author Dirk Weigenand
 */
interface ComponentConfigurationReader {
    /**
     * Read configuration file from the given reader and update the given
     * component with it.
     * 
     * @param component
     *            development component to update with the information from the
     *            given reader.
     * @param reader
     *            reader for configuration file.
     */
    void execute(DevelopmentComponent component, Reader reader);
}
