/**
 *
 */
package org.arachna.netweaver.dctool;

import java.util.List;

/**
 * Interface for generators for DC tool commands.
 * 
 * @author Dirk Weigenand
 */
public interface DCToolCommandBuilder {
    /**
     * build DC tool commands and return them.
     * 
     * @return generated DC tool commands.
     */
    List<String> execute();
}
