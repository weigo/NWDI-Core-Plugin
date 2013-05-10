/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.changelog;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Collection;

import org.arachna.netweaver.hudson.dtr.browser.Activity;

/**
 * Service for dealing with DTR change logs.
 * 
 * @author Dirk Weigenand
 */
public final class ChangeLogService {
    /**
     * Write the given activities as change log into the given file.
     * 
     * @param build
     *            build to use for creating the {@link ChangeLogSet}.
     * @param changelogFile
     *            file to write change log into.
     * @param activities
     *            activities to persist as change sets.
     * @throws IOException
     *             when writing the log fails.
     */
    public void writeChangeLog(final AbstractBuild<?, ?> build, final File changelogFile,
        final Collection<Activity> activities) throws IOException {
        final DtrChangeLogWriter dtrChangeLogWriter =
            new DtrChangeLogWriter(new DtrChangeLogSet(build, activities), new OutputStreamWriter(new FileOutputStream(
                changelogFile), "UTF-8"));
        dtrChangeLogWriter.write();
    }

    /**
     * Create a DTR change log parser.
     * 
     * @return a new DTR change log parser.
     */
    public ChangeLogParser createChangeLogParser() {
        return new DtrChangeLogParser();
    }
}
