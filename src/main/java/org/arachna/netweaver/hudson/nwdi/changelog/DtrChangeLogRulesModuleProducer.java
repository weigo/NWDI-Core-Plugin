/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.changelog;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogEntry.Action;
import org.arachna.netweaver.hudson.nwdi.changelog.DtrChangeLogEntry.Item;
import org.arachna.xml.RulesModuleProducer;
import org.xml.sax.Attributes;

/**
 * Producer for rules to parse a DTR change log file.
 * 
 * @author Dirk Weigenand
 */
public class DtrChangeLogRulesModuleProducer implements RulesModuleProducer {
    /**
     * method name for adding items.
     */
    private static final String ADD_METHOD_NAME = "add";

    /**
     * Create rules module for parsing a DTR changelog.
     * 
     * @return rules module for parsing a DTR changelog.
     */
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("changelog/changeset").factoryCreate().usingFactory(new DtrChangeLogEntryFactory()).then()
                    .setNext(ADD_METHOD_NAME);
                forPattern("changelog/changeset/date").callMethod("setCheckInTime").usingElementBodyAsArgument();
                forPattern("changelog/changeset/user").setBeanProperty().withName("user");
                forPattern("changelog/changeset/comment").setBeanProperty().withName("msg");
                forPattern("changelog/changeset/description").setBeanProperty().withName("description");
                forPattern("changelog/changeset/items/item").addRule(new ItemFactoryRule()).then()
                    .setNext(ADD_METHOD_NAME);
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
