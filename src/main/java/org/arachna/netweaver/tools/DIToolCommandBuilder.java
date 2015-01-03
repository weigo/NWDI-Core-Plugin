/**
 *
 */
package org.arachna.netweaver.tools;

import java.util.List;

/**
 * Interface for generators for NWDI tool commands.
 * 
 * @author Dirk Weigenand
 */
public interface DIToolCommandBuilder {
    /**
     * build DC tool commands and return them.
     * 
     * @return generated DC tool commands.
     */
    List<String> execute();
}
