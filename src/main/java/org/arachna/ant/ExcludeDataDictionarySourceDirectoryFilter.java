/**
 *
 */
package org.arachna.ant;

import java.util.regex.Pattern;

/**
 * Filter source folders based on folder name. Exclude folders containing
 * 'gen_ddic/datatypes'.
 * 
 * @author Dirk Weigenand
 */
public final class ExcludeDataDictionarySourceDirectoryFilter implements SourceDirectoryFilter {
    /**
     * regexp for the 'gen_ddic/datatypes' folder name.
     */
    private final Pattern pattern = Pattern.compile(".*?[\\\\/]gen_ddic[\\\\/]datatypes");

    /**
     * Ignore folders containing generated sources of data dictionary classes.
     * 
     * @param folderName
     *            absolute path name of source folder.
     * @return <code>true</code> when the given path does not contain
     *         'gen_ddic', <code>false</code> otherwise.
     */
    public boolean accept(final String folderName) {
        return !pattern.matcher(folderName).matches();
    }
}
