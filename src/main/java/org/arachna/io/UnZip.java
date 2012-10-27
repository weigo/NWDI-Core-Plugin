/**
 *
 */
package org.arachna.io;

import hudson.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * UnZip utility class.
 * 
 * @author G526521
 */
public final class UnZip {
    /**
     * target folder where zip file should be unpacked.
     */
    private final File targetDirectory;

    /**
     * name of zip file that should be unzipped.
     */
    private final String zipFileName;

    /**
     * create an Unzip object with a target directory name and the name of the
     * file to unzip.
     * 
     * @param targetDirectory
     *            target directory where the given file shall be unzipped to. If
     *            null or empty the given file will be unzipped into the current
     *            directory i.e. the directory the jvm was started from.
     * @param zipFileName
     *            name of file to be unzipped.
     */
    public UnZip(final String targetDirectory, final String zipFileName) {
        this.targetDirectory = getTargetDirectory(targetDirectory);
        this.zipFileName = zipFileName;
    }

    /**
     * Get the target directory where the zip file should be unzipped. If the
     * given target directory is null or empty the user's home directory is
     * used.
     * 
     * @param targetDirectory
     *            target directory where the zip file should be unzipped.
     * @return target directory where the zip file should be unzipped.
     */
    private File getTargetDirectory(final String targetDirectory) {
        String targetDir = targetDirectory;

        if (targetDir == null || targetDir.trim().length() == 0) {
            targetDir = System.getProperty("user.dir");
        }

        return new File(targetDir);
    }

    /**
     * unzip the file given in the constructor into the given target directory.
     * 
     * @throws IOException
     *             when the given target directory does not exist or is not a
     *             directory
     */
    public void execute() throws IOException {
        validateTargetDirectory();

        final ZipFile archive = new ZipFile(zipFileName);
        final Enumeration<? extends ZipEntry> entries = archive.entries();
        ZipEntry entry;

        while (entries.hasMoreElements()) {
            entry = entries.nextElement();

            final String pathName = targetDirectory.getAbsolutePath() + File.separatorChar + entry.getName();

            if (entry.isDirectory()) {
                createDirectory(new File(pathName));
            }
            else {
                final File target = new File(pathName);
                createDirectory(target.getParentFile());

                Util.copyStreamAndClose(archive.getInputStream(entry), new FileOutputStream(target));
            }
        }
    }

    /**
     * Create the given directory or fail with a RuntimeException.
     * 
     * @param directory
     *            directory to create.
     */
    private void createDirectory(final File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            System.err.println("could not create " + directory.getAbsolutePath() + "!");
        }
    }

    /**
     * Validates that the target directory exists and is indeed a directory.
     * 
     * @throws IOException
     *             if the target directory does not exist or is not directory.
     */
    private void validateTargetDirectory() throws IOException {
        if (!targetDirectory.exists()) {
            throw new FileNotFoundException("directory " + targetDirectory.getAbsolutePath() + " does not exist!");
        }
        else if (!targetDirectory.isDirectory()) {
            throw new IOException(targetDirectory.getAbsolutePath() + " is not a directory!");
        }
    }
}
