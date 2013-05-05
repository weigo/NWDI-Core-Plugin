/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.changelog;

import hudson.model.AbstractBuild;
import hudson.scm.ChangeLogParser;
import hudson.scm.ChangeLogSet;
import hudson.scm.ChangeLogSet.Entry;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogEntry.Action;
import org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogEntry.Item;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * A parser for a DTR change log persisted to XML.
 * 
 * @author Dirk Weigenand
 */
final class DtrChangeLogParser extends ChangeLogParser {
    @Override
    public ChangeLogSet<? extends Entry> parse(final AbstractBuild build, final File changelogFile) throws IOException,
        SAXException {
        final DtrChangeLogSet changeSet = new DtrChangeLogSet(build);

        parse(changeSet, new FileReader(changelogFile));

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
        try {
            final DigesterLoader digesterLoader = DigesterLoader.newLoader(getRulesModule());
            final Digester digester = digesterLoader.newDigester();
            digester.push(changeSet);
            digester.parse(reader);

        }
        catch (final SAXException e) {
            throw new IllegalStateException(e);
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Create rules module for parsing a DTR changelog.
     * 
     * @return rules module for parsing a DTR changelog.
     */
    private RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("changelog/changeset").factoryCreate().usingFactory(new DtrChangeLogEntryFactory()).then()
                    .setNext("add");
                forPattern("changelog/changeset/date").callMethod("setCheckInTime").usingElementBodyAsArgument();
                forPattern("changelog/changeset/user").setBeanProperty().withName("user");
                forPattern("changelog/changeset/comment").setBeanProperty().withName("msg");
                forPattern("changelog/changeset/description").setBeanProperty().withName("description");
                forPattern("changelog/changeset/items/item").addRule(new ItemFactoryRule()).then().setNext("add");
            }
        };
    }

    /**
     * @author Dirk Weigenand
     * 
     */
    private static class DtrChangeLogEntryFactory extends AbstractObjectCreationFactory<DtrChangeLogEntry> {
        @Override
        public DtrChangeLogEntry createObject(final Attributes attributes) throws Exception {
            final DtrChangeLogEntry entry = new DtrChangeLogEntry();
            entry.setActivityUrl(attributes.getValue("activityUrl"));

            return entry;
        }
    }

    /**
     * Rule for creating action items.
     * 
     * @author Dirk Weigenand
     */
    private static class ItemFactoryRule extends Rule {
        /**
         * The action of the item to create.
         */
        private Action action;

        @Override
        public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
            action = DtrChangeLogEntry.Action.fromString(attributes.getValue("action"));
        }

        @Override
        public void body(final String namespace, final String name, final String text) throws Exception {
            getDigester().push(new Item(text, action));
        }

        @Override
        public void end(final String namespace, final String name) throws Exception {
            getDigester().pop();
            super.end(namespace, name);
        }

        @Override
        public void finish() throws Exception {
            action = null;
            super.finish();
        }
    }
}
