/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartType;
import org.arachna.xml.RulesModuleProducer;

/**
 * Reader for PublicPart descriptions.
 * 
 * @author Dirk Weigenand
 */
final class PublicPartRulesModuleProducer implements RulesModuleProducer {
    /**
     * Rule to parse the purpose of a public part, i.e. its type.
     * 
     * @author Dirk Weigenand
     */
    private static final class PurposeRule extends Rule {
        @Override
        public void body(final String namespace, final String name, final String text) throws Exception {
            getDigester().push(PublicPartType.fromString(text));
        }

        @Override
        public void end(final String namespace, final String name) throws Exception {
            getDigester().pop();
            super.end(namespace, name);
        }
    }

    /**
     * Return a {@link RulesModule} for parsing public part definitions.
     * 
     * {@inheritDoc}
     */
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("public-part").createObject().ofType(PublicPart.class);
                forPattern("public-part/name").setBeanProperty().withName("publicPart");
                forPattern("public-part/caption").setBeanProperty().withName("caption");
                forPattern("public-part/description").setBeanProperty().withName("description");
                forPattern("public-part/purpose").addRule(new PurposeRule()).then().setNext("setType");
            }
        };
    }
}
