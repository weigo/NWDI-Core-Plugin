/**
 * 
 */
package org.arachna.ant;

/**
 * An all source folder names accepting filter.
 * 
 * @author Dirk Weigenand
 */
public final class AllSourceFoldersAcceptingFilter implements SourceDirectoryFilter {

    /**
     * Accept all given folder names that are not <code>null</code> or empty.
     * 
     * @param folderName
     *            the name of a folder containing source files.
     * @return <code>true</code> when the given name is not <code>null</code>
     *         and not empty, <code>false</code> otherwise.
     */
    public boolean accept(final String folderName) {
        return folderName != null && !folderName.isEmpty();
    }
}
