/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.Util;

import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;

import org.arachna.netweaver.hudson.nwdi.DtrChangeLogEntry.Item;

/**
 * Persist a {@link DtrChangeLogSet} to a file as XML.
 * 
 * @author Dirk Weigenand
 */
public class DtrChangeLogWriter {
    /**
     * the writer to write the XML into.
     */
    private final Writer changeLog;

    /**
     * change set to persist to XML.
     */
    private final DtrChangeLogSet changeSet;

    /**
     * Create an instance of a <code></code> using the given {@link DtrChangeLogSet} and writer.
     * 
     * @param changeSet
     *            change set to persist to XML.
     * @param changeLog
     *            the writer to write the XML into.
     */
    DtrChangeLogWriter(final DtrChangeLogSet changeSet, final Writer changeLog) {
        this.changeSet = changeSet;
        this.changeLog = changeLog;
    }

    /**
     * Write the change set as XML into the writer given when this writer was created.
     * 
     * Closes the writer at the end, so calling it more than once will result in an exception.
     * 
     * @throws IOException
     *             when an error occurs writing the XML.
     */
    void write() throws IOException {
        changeLog.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        changeLog.write("<changelog>\n");
        final SimpleDateFormat format = new SimpleDateFormat(DtrChangeLogEntry.DATE_FORMAT_SPEC);

        for (final DtrChangeLogEntry entry : this.changeSet) {
            changeLog
                .write(String.format("\t<changeset activityUrl=\"%s\">\n", Util.xmlEscape(entry.getActivityUrl())));
            changeLog.write(String.format("\t\t<date>%s</date>\n", format.format(entry.getCheckInTime())));
            changeLog.write(String.format("\t\t<user>%s</user>\n", entry.getAuthor()));
            changeLog.write(String.format("\t\t<comment>%s</comment>\n", entry.getMsg()));
            changeLog.write(String.format("\t\t<description>%s</description>\n", entry.getDescription()));
            changeLog.write("\t\t<items>\n");

            for (final Item item : entry.getItems()) {
                changeLog
                    .write(String.format("\t\t\t<item action=\"%s\">%s</item>\n", item.getAction(), item.getPath()));
            }

            changeLog.write("\t\t</items>\n");
            changeLog.write("\t</changeset>\n");
        }

        changeLog.write("</changelog>\n");
        changeLog.close();
    }
}
