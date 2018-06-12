/**
 *
 */
package org.arachna.util.io;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Finder for file objects. Scans the given directory recursively for files
 * whose names match the given pattern.
 *
 * @author Dirk Weigenand
 */
public final class FileFinder {

    /**
     * base directory from where to start the search.
     */
    private final File baseDir;

    /**
     * Filter used to filter out directories.
     */
    private final DirectoryFilter directoryFilter = new DirectoryFilter();

    /**
     * Filter for file names matching a regular expression.
     */
    private final FileNameFilter fileNameFilter;

    /**
     * List of matching files.
     */
    private final List<File> matchingFiles = new ArrayList<File>();

    /**
     * create a FileFinder instance with a base directory from where to start
     * the search and a regular expression to match file names against.
     *
     * @param baseDir base directory from where to start the search from
     * @param regex   regular expression to match file names against
     */
    public FileFinder(final File baseDir, final String regex) {
        this.baseDir = baseDir;
        this.fileNameFilter = new FileNameFilter(regex);
    }

    /**
     * Scans recursively for files that match the given regular expression.
     *
     * @return List&lt;File&gt; of matching files.
     */
    public List<File> find() {
        this.matchingFiles.clear();
        this.find(this.baseDir);

        return this.matchingFiles;
    }

    /**
     * search for files matching <code>this.pattern</code> recursively starting
     * at <code>baseDir</code>.
     *
     * @param baseDir directory where to start scanning for matching files.
     * @return list of files matching pattern or empty list
     */
    private List<File> find(final File baseDir) {
        findInDirectories(baseDir.listFiles(this.directoryFilter));
        addMatchingFiles(baseDir.listFiles(this.fileNameFilter));

        return matchingFiles;
    }

    /**
     * Add the given entries to the list of matching files if argument ist not
     * <code>null</code>.
     *
     * @param entries files to add to the list of matching files
     */
    private void addMatchingFiles(final File[] entries) {
        if (entries != null) {
            this.matchingFiles.addAll(Arrays.asList(entries));
        }
    }

    /**
     * Filter files in the given sub directories.
     *
     * @param entries sub directories to filter files in.
     */
    private void findInDirectories(final File[] entries) {
        if (entries != null) {
            for (File entry : entries) {
                this.find(entry);
            }
        }
    }
}
