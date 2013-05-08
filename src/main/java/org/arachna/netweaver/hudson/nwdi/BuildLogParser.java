/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

import org.arachna.util.io.FileFinder;

/**
 * Parser for build.log files. Extracts source folders and folders where class
 * files are generated to from <code>build.log</code> files.
 * 
 * @author Dirk Weigenand
 */
class BuildLogParser {
    /**
     * indicates the location of source paths in 'build.log'.
     */
    private static final String SOURCE_PATHS = "source paths:";

    /**
     * indicates the location of a source path in 'build.log'.
     */
    private static final String SOURCE_PATH = "source path:";

    /**
     * indicates an output directory where class files are generated to.
     */
    private static final String OUTPUT_DIR = "output dir:";

    /**
     * Set of source folders.
     */
    private final Set<String> sourceFolders = new HashSet<String>();

    /**
     * folder where the project lives.
     */
    private final String projectDirectory;

    /**
     * folders where class files are generated to.
     */
    private String outputFolder = "";

    /**
     * Create a BuildLogParser instance with the given project directory.
     * 
     * @param projectDirectory
     *            project folder
     */
    BuildLogParser(final String projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    /**
     * Parse the <code>build.log</code> file.
     */
    void parse() {
        BufferedReader reader = null;

        try {
            reader =
                new BufferedReader(new InputStreamReader(new FileInputStream(new File(projectDirectory,
                    "gen/default/logs/build.log")), Charset.forName("UTF8")));

            parseBuildLog(reader);
        }
        catch (final FileNotFoundException e) {
            // ignore, maybe there was a build error so the file does not exist.
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            if (reader != null) {
                try {
                    reader.close();
                }
                catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Parse the build log given in the specified reader.
     * 
     * @param reader
     *            reader containing the build log.
     * @throws IOException
     */
    protected void parseBuildLog(final BufferedReader reader) throws IOException {
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.contains(SOURCE_PATHS)) {
                readMultipleSourcePaths(reader);
            }
            else if (line.contains(SOURCE_PATH)) {
                readSingleSourcePath(line);
            }
            else if (line.contains(OUTPUT_DIR)) {
                readOutputDirectory(line);
            }
        }
    }

    /**
     * Read the output directory from the given line.
     * 
     * @param line
     *            line of build.log to extract output directory from.
     */
    private void readOutputDirectory(final String line) {
        final int index = line.indexOf(OUTPUT_DIR);
        outputFolder = normalizeFileName(line.substring(index + OUTPUT_DIR.length()).trim());
    }

    /**
     * Extract a source path from the given line and store it into {@see
     * #sourceFolders}.
     * 
     * @param line
     *            the build.log line to extract a source path from.
     */
    private void readSingleSourcePath(final String line) {
        final int sourcePathIndex = line.indexOf(SOURCE_PATH);
        final String folderName = normalizeFileName(line.substring(sourcePathIndex + SOURCE_PATH.length()).trim());

        if (folderContainsJavaSources(folderName)) {
            sourceFolders.add(folderName);
        }
    }

    /**
     * Read the source paths from the given reader. Source paths are stored in
     * {@see #sourceFolders}.
     * 
     * @param reader
     *            reader to use reading the build.log file.
     * @throws IOException
     *             throw any IOException thrown reading the build log.
     */
    private void readMultipleSourcePaths(final BufferedReader reader) throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.contains("class path:")) {
                break;
            }

            final String folderName = normalizeFileName(line.replace("[echo]", "").trim());

            if (folderContainsJavaSources(folderName)) {
                sourceFolders.add(folderName);
            }
        }
    }

    /**
     * Normalize the given file name with respect to the path separator char
     * matching the operating system the program is currently running on.
     * 
     * @param fileName
     *            file name to normalize.
     * @return the normalized file name.
     */
    private String normalizeFileName(final String fileName) {
        return fileName.replace('\\', File.separatorChar).replace('/', File.separatorChar);
    }

    /**
     * Checks whether the given path contains '.java' source files.
     * 
     * @param folderName
     *            folder to check for java source files.
     * 
     * @return <code>true</code> if the given path contains java sources,
     *         <code>false</code> otherwise.
     */
    private boolean folderContainsJavaSources(final String folderName) {
        final String sourceRegex = ".*?\\.java";
        final File baseDir = new File(folderName);

        final FileFinder javaSourceFilesFinder = new FileFinder(baseDir, sourceRegex);

        return !javaSourceFilesFinder.find().isEmpty();
    }

    /**
     * @return the sourceFolders
     */
    public Set<String> getSourceFolders() {
        return sourceFolders;
    }

    /**
     * @return the outputFolder
     */
    public String getOutputFolder() {
        return outputFolder;
    }
}
