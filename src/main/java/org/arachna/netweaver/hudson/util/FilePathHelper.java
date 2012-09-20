/**
 *
 */
package org.arachna.netweaver.hudson.util;

import hudson.FilePath;

import java.io.File;
import java.util.Stack;

/**
 * Helper for {@link FilePath} objects.
 * 
 * @author Dirk Weigenand
 * 
 */
public final class FilePathHelper {
    /**
     * Utility classes shall not be instantiated.
     */
    private FilePathHelper() {
    }

    /**
     * Returns the absolute path of the given <code>path</code>.
     * 
     * @param path
     *            the {@link FilePath} to compute the absolute path for.
     * @return absolute path for the given file path.
     */
    public static String makeAbsolute(final FilePath path) {
        final Stack<String> paths = new Stack<String>();
        FilePath parent = path;

        while (parent != null) {
            paths.push(parent.getName());
            parent = parent.getParent();
        }

        final StringBuilder absolutePath = new StringBuilder();

        while (!paths.isEmpty()) {
            absolutePath.append(paths.pop());

            if (!paths.isEmpty()) {
                absolutePath.append(File.separatorChar);
            }
        }

        return absolutePath.toString();
    }
}
