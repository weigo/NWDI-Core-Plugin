/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.changelog;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
            new DtrChangeLogWriter(new DtrChangeLogSet(build, activities), new FileWriter(changelogFile));
        dtrChangeLogWriter.write();
    }

    public ChangeLogParser createChangeLogParser() {
        return new DtrChangeLogParser();
    }
}
