package org.arachna.util.io;

import java.io.File;
import java.io.FileFilter;

/**
 * Filter for directories.
 * 
 * @author weigo
 */
public final class DirectoryFilter implements FileFilter {
    /**
     * Test whether the given {@link java.io.File} is a directory.
     * 
     * @param pathname
     *            the file to test
     * @see java.io.FileFilter#accept(java.io.File)
     * @return <code>true</code> when the given pathname is a directory
     *         <code>false</code> otherwise.
     */
    public boolean accept(final File pathname) {
        return pathname.isDirectory();
    }
}
