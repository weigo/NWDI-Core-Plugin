/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.arachna.netweaver.hudson.nwdi.DtrChangeLogEntry.Item;
import org.arachna.xml.AbstractDefaultHandler;
import org.arachna.xml.XmlReaderHelper;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A parser for a DTR change log persisted to XML.
 * 
 * @author Dirk Weigenand
 */
public final class DtrChangeLogParser extends ChangeLogParser {
    /**
     * {@inheritDoc}
     */
    @Override
    public ChangeLogSet<? extends Entry> parse(final AbstractBuild build, final File changelogFile) throws IOException,
        SAXException {
        // Do the actual parsing
        final DtrChangeLogSet changeSet = new DtrChangeLogSet(build);
        FileReader reader = null;

        try {
            reader = new FileReader(changelogFile);
            new XmlReaderHelper(new InternalDtrChangeLogParser(changeSet)).parse(reader);
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }

        return changeSet;
    }

    /**
     * SAX parser for reading DTR changelog files.
     * 
     * @author Dirk Weigenand
     */
    private static final class InternalDtrChangeLogParser extends AbstractDefaultHandler {
        /**
         * root element of a dtr changelog.
         */
        private static final String CHANGELOG = "changelog";

        /**
         * 'action' element.
         */
        private static final String ACTION = "action";

        /**
         * changeset 'version' attribute.
         */
        private static final String VERSION = "version";

        /**
         * 'item' element.
         */
        private static final String ITEM = "item";

        /**
         * 'date' element.
         */
        private static final String DATE = "date";

        /**
         * 'user' element.
         */
        private static final String USER = "user";

        /**
         * 'description' element.
         */
        private static final String DESCRIPTION = "description";

        /**
         * 'comment' element.
         */
        private static final String COMMENT = "comment";

        /**
         * 'changeset' element.
         */
        private static final String CHANGESET = "changeset";

        /**
         * changeset being read.
         */
        private final DtrChangeLogSet changeSet;

        /**
         * the changelog entry being read.
         */
        private DtrChangeLogEntry currentChangeLogEntry;

        /**
         * the Action denoted by the current changelog entry.
         */
        private DtrChangeLogEntry.Action currentItemAction;

        /**
         * Create an instance of a {@link InternalDtrChangeLogParser} using the
         * given changeset.
         * 
         * @param changeSet
         *            the changeset to add the read entries to.
         */
        InternalDtrChangeLogParser(final DtrChangeLogSet changeSet) {
            super(null);
            this.changeSet = changeSet;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
         * java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            if (CHANGESET.equals(localName)) {
                changeSet.add(currentChangeLogEntry);
                currentChangeLogEntry = null;
            }
            else if (COMMENT.equals(localName)) {
                currentChangeLogEntry.setMsg(getText());
            }
            else if (DESCRIPTION.equals(localName)) {
                currentChangeLogEntry.setDescription(getText());
            }
            else if (USER.equals(localName)) {
                currentChangeLogEntry.setUser(getText());
            }
            else if (DATE.equals(localName)) {
                currentChangeLogEntry.setCheckInTime(getText());
            }
            else if (ITEM.equals(localName)) {
                currentChangeLogEntry.add(new Item(getText(), currentItemAction));
                currentItemAction = null;
            }
            else if (CHANGELOG.equals(localName)) {
                changeSet.sort();
            }

            getText();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
         * java.lang.String, java.lang.String, org.xml.sax.Attributes)
         */
        @Override
        public void startElement(final String uri, final String localName, final String qName,
            final Attributes attributes) throws SAXException {
            if (CHANGESET.equals(localName)) {
                currentChangeLogEntry = new DtrChangeLogEntry();
                currentChangeLogEntry.setVersion(attributes.getValue(VERSION));
            }
            else if (ITEM.equals(localName)) {
                currentItemAction = DtrChangeLogEntry.Action.fromString(attributes.getValue(ACTION));
            }
        }
    }
}
