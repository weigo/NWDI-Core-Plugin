/**
 *
 */
package org.arachna.ant;

/**
 * Filter for source directories of development components.
 *
 * @author Dirk Weigenand
 */
public interface SourceDirectoryFilter {
    /**
     * Implement to filter out certain source directories.
     *
     * @param folderName
     *            source path to validate
     * @return <code>true</code> when the given source path is acceptable for
     *         the given type of development component.
     */
    boolean accept(String folderName);
}
