/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.changelog;

import hudson.Util;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;

import org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogEntry.Item;

/**
 * Persist a {@link DtrChangeLogSet} to a file as XML.
 * 
 * @author Dirk Weigenand
 */
class DtrChangeLogWriter {
    /**
     * the writer to write the XML into.
     */
    private final BufferedWriter changeLog;

    /**
     * change set to persist to XML.
     */
    private final DtrChangeLogSet changeSet;

    /**
     * Create an instance of a <code></code> using the given
     * {@link DtrChangeLogSet} and writer.
     * 
     * @param changeSet
     *            change set to persist to XML.
     * @param changeLog
     *            the writer to write the XML into.
     */
    DtrChangeLogWriter(final DtrChangeLogSet changeSet, final Writer changeLog) {
        this.changeSet = changeSet;
        this.changeLog = new BufferedWriter(changeLog);
    }

    /**
     * Write the change set as XML into the writer given when this writer was
     * created.
     * 
     * Closes the writer at the end, so calling it more than once will result in
     * an exception.
     * 
     * @throws IOException
     *             when an error occurs writing the XML.
     */
    void write() throws IOException {
        final SimpleDateFormat format = new SimpleDateFormat(DtrChangeLogEntry.DATE_FORMAT_SPEC);
        changeLog.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        changeLog.newLine();
        changeLog.write("<changelog>");
        changeLog.newLine();

        for (final DtrChangeLogEntry entry : changeSet) {
            changeLog.write(String.format("  <changeset activityUrl=\"%s\">", Util.xmlEscape(entry.getActivityUrl())));
            changeLog.newLine();
            changeLog.write(String.format("    <date>%s</date>", format.format(entry.getCheckInTime())));
            changeLog.newLine();
            changeLog.write(String.format("    <user>%s</user>", entry.getAuthor()));
            changeLog.newLine();
            changeLog.write(String.format("    <comment>%s</comment>", entry.getMsg()));
            changeLog.newLine();
            changeLog.write(String.format("    <description>%s</description>", entry.getDescription()));
            changeLog.newLine();
            changeLog.write("    <items>");
            changeLog.newLine();

            for (final Item item : entry.getItems()) {
                changeLog.write(String.format("      <item action=\"%s\">%s</item>", item.getAction(), item.getPath()));
                changeLog.newLine();
            }

            changeLog.write("    </items>");
            changeLog.newLine();
            changeLog.write("  </changeset>");
            changeLog.newLine();
        }

        changeLog.write("</changelog>");
        changeLog.newLine();
        changeLog.close();
    }
}
