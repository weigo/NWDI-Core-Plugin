/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.arachna.netweaver.hudson.dtr.browser.Activity;
import org.kohsuke.stapler.export.Exported;
import org.kohsuke.stapler.export.ExportedBean;

/**
 * A log of a set of changes in the DTR.
 * 
 * @author Dirk Weigenand
 */
@ExportedBean(defaultVisibility = 999)
public final class DtrChangeLogSet extends ChangeLogSet<DtrChangeLogEntry> {
    /**
     * Entries in the change log.
     */
    private final List<DtrChangeLogEntry> entries = new ArrayList<DtrChangeLogEntry>();

    /**
     * Creat an instance of a <code>DtrChangeLogSet</code>.
     * 
     * @param build
     *            the build for which to create the change log.
     * @param activities
     *            activities to create the entries from.
     */
    public DtrChangeLogSet(final AbstractBuild<?, ?> build, final Collection<Activity> activities) {
        super(build);

        for (final Activity activity : activities) {
            this.add(new DtrChangeLogEntry(activity));
        }
    }

    /**
     * Convenience constructor using only the build.
     * 
     * @param build
     *            the build for which to create the change log.
     */
    public DtrChangeLogSet(final AbstractBuild<?, ?> build) {
        super(build);
    }

    @Exported
    @Override
    public boolean isEmptySet() {
        return entries.isEmpty();
    }

    /**
     * Return an {@link Iterator} over the change log entries of this change log
     * set. {@inheritDoc}
     */
    public Iterator<DtrChangeLogEntry> iterator() {
        return entries.iterator();
    }

    /**
     * Add all change log entries to this change log set.
     * 
     * @param entries
     *            change log entries to add to this change log set.
     */
    void add(final List<DtrChangeLogEntry> entries) {
        for (final DtrChangeLogEntry entry : entries) {
            this.add(entry);
        }
    }

    /**
     * Add the given change log entry to this change log set.
     * 
     * @param entry
     *            change log entry to add.
     */
    public void add(final DtrChangeLogEntry entry) {
        entry.setParent(this);
        entries.add(entry);
    }

    /**
     * Sort the change log entries by their check in time.
     */
    void sort() {
        Collections.sort(entries, new DtrChangeLogEntryComparator());
    }

    /**
     * {@link Comparator} for change log entries by check in time.
     * 
     * @author Dirk Weigenand
     */
    private final class DtrChangeLogEntryComparator implements Comparator<DtrChangeLogEntry> {
        /**
         * {@inheritDoc}
         */
        public int compare(final DtrChangeLogEntry entry1, final DtrChangeLogEntry entry2) {
            return entry1.getCheckInTime().compareTo(entry2.getCheckInTime());
        }
    }
}
