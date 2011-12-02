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
 * @author G526521
 */
@ExportedBean(defaultVisibility = 999)
public final class DtrChangeLogSet extends ChangeLogSet<DtrChangeLogEntry> {
    /**
     * Entries in the change log.
     */
    private List<DtrChangeLogEntry> entries = new ArrayList<DtrChangeLogEntry>();

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
        return this.entries.isEmpty();
    }

    public Iterator<DtrChangeLogEntry> iterator() {
        return this.entries.iterator();
    }

    void add(final List<DtrChangeLogEntry> entries) {
        for (final DtrChangeLogEntry entry : entries) {
            this.add(entry);
        }
    }

    public void add(final DtrChangeLogEntry entry) {
        entry.setParent(this);
        this.entries.add(entry);
    }

    void sort() {
        Collections.sort(this.entries, new DtrChangeLogEntryComparator());
    }

    private final class DtrChangeLogEntryComparator implements Comparator<DtrChangeLogEntry> {
        /**
         * {@inheritDoc}
         */
        public int compare(DtrChangeLogEntry entry1, DtrChangeLogEntry entry2) {
            return entry1.getCheckInTime().compareTo(entry2.getCheckInTime());
        }
    }
}
