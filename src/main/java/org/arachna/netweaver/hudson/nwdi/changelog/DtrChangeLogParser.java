/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.changelog;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.arachna.xml.DigesterHelper;
import org.xml.sax.SAXException;

/**
 * A parser for a DTR change log persisted to XML.
 * 
 * @author Dirk Weigenand
 */
public final class DtrChangeLogParser extends ChangeLogParser {
    @Override
    public ChangeLogSet<? extends Entry> parse(final AbstractBuild build, final File changelogFile) throws IOException,
        SAXException {
        final DtrChangeLogSet changeSet = new DtrChangeLogSet(build);

        parse(changeSet, new InputStreamReader(new FileInputStream(changelogFile), Charset.forName("UTF-8")));

        return changeSet;
    }

    /**
     * Parse the given change log and update the set of changes with change sets
     * from it..
     * 
     * @param changeSet
     *            change set to update.
     * @param reader
     *            reader to parse change set entries from.
     */
    protected void parse(final DtrChangeLogSet changeSet, final Reader reader) {
        new DigesterHelper<DtrChangeLogSet>(new DtrChangeLogRulesModuleProducer()).update(reader, changeSet);
    }
}
