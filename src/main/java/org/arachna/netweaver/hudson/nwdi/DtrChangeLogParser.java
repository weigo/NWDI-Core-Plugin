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
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * A parser for a DTR change log persisted to XML.
 * 
 * @author Dirk Weigenand
 */
public final class DtrChangeLogParser extends ChangeLogParser {
    @Override
    public ChangeLogSet<? extends Entry> parse(final AbstractBuild build, final File changelogFile) throws IOException,
        SAXException {
        // Do the actual parsing
        final DtrChangeLogSet changeSet = new DtrChangeLogSet(build);
        FileReader reader = null;

        try {
            reader = new FileReader(changelogFile);
            final XMLReader xmlReader = XMLReaderFactory.createXMLReader();
            xmlReader.setContentHandler(new InternalDtrChangeLogParser(changeSet));
            xmlReader.parse(new InputSource(reader));
        }
        finally {
            if (reader != null) {
                reader.close();
            }
        }

        return changeSet;
    }

    private static final class InternalDtrChangeLogParser extends DefaultHandler {
        /**
         * Textcontainer for text elements.
         */
        private final StringBuilder text = new StringBuilder();

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

        InternalDtrChangeLogParser(final DtrChangeLogSet changeSet) {
            this.changeSet = changeSet;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.helpers.DefaultHandler#characters(char[], int, int)
         */
        @Override
        public void characters(final char[] ch, final int start, final int length) throws SAXException {
            text.append(ch, start, length);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
         * java.lang.String, java.lang.String)
         */
        @Override
        public void endElement(final String uri, final String localName, final String qName) throws SAXException {
            if ("changeset".equals(localName)) {
                this.changeSet.add(this.currentChangeLogEntry);
                this.currentChangeLogEntry = null;
            }
            else if ("comment".equals(localName)) {
                this.currentChangeLogEntry.setMsg(this.getText());
            }
            else if ("description".equals(localName)) {
                this.currentChangeLogEntry.setDescription(this.getText());
            }
            else if ("user".equals(localName)) {
                this.currentChangeLogEntry.setUser(getText());
            }
            else if ("date".equals(localName)) {
                this.currentChangeLogEntry.setCheckInTime(getText());
            }
            else if ("item".equals(localName)) {
                this.currentChangeLogEntry.add(new Item(getText(), this.currentItemAction));
                this.currentItemAction = null;
            }

            this.getText();
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
            if ("changeset".equals(localName)) {
                this.currentChangeLogEntry = new DtrChangeLogEntry();
                this.currentChangeLogEntry.setVersion(attributes.getValue("version"));
            }
            else if ("item".equals(localName)) {
                this.currentItemAction = DtrChangeLogEntry.Action.fromString(attributes.getValue("action"));
            }
        }

        /**
         * Returns the text contained in the last element and resets the text
         * buffer.
         * 
         * @return text contained in the last element
         */
        private String getText() {
            final String t = this.text.toString().trim();
            this.text.setLength(0);

            return t;
        }
    }
}
