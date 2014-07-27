package org.arachna.util.io;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

/**
 * Filter for file names.
 * 
 * @author weigo
 */
public final class FileNameFilter implements FilenameFilter {
    /**
     * pattern to match file names against.
     */
    private final Pattern pattern;

    /**
     * Create an instance of a FileNameFilter.
     * 
     * @param regex
     *            regular expression to use for filtering files.
     */
    FileNameFilter(final String regex) {
        this.pattern = Pattern.compile(regex);
    }

    /**
     * Validate the given name agains the regular expression given in the
     * constructor.
     * 
     * @param dir
     *            unused, present in order to satisfy the interface
     * @param name
     *            file name to validate against regular expression to filter
     *            against
     * @return <code>true</code> when the file name matches the regular
     *         expression in the constructor, <code>false</code> otherwise.
     */
    public boolean accept(final File dir, final String name) {
        return this.pattern.matcher(name).matches();
    }
}
