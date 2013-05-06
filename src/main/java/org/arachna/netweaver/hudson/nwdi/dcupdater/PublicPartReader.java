/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartType;
import org.xml.sax.SAXException;

/**
 * Reader for PublicPart descriptions.
 * 
 * @author Dirk Weigenand
 */
final class PublicPartReader {
    /**
     * RulesModule for parsing public part definitions.
     * 
     * @author Dirk Weigenand
     */
    private static class PublicPartModule extends AbstractRulesModule {
        @Override
        protected void configure() {
            forPattern("public-part").createObject().ofType(PublicPart.class);
            forPattern("public-part/name").setBeanProperty().withName("publicPart");
            forPattern("public-part/caption").setBeanProperty().withName("caption");
            forPattern("public-part/description").setBeanProperty().withName("description");
            forPattern("public-part/purpose").addRule(new PurposeRule()).then().setNext("setType");
        }
    }

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
     * Parse a {@link PublicPart} object from the given reader.
     * 
     * @param reader
     *            reader containing a public part definition.
     * @return the public part.
     */
    public PublicPart execute(final Reader reader) {
        try {
            final DigesterLoader digesterLoader = DigesterLoader.newLoader(new PublicPartModule());
            final Digester digester = digesterLoader.newDigester();

            return (PublicPart)digester.parse(reader);
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
}
