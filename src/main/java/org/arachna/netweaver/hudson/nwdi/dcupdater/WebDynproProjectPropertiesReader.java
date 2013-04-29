/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.DigesterLoader;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Reader for 'ProjectProperties.wdproperties' configuration files.
 * 
 * @author Dirk Weigenand
 */
final class WebDynproProjectPropertiesReader implements ComponentConfigurationReader {
    /**
     * prefix used to reference portal applications/services.
     */
    private static final String PORTAL_PREFIX = "PORTAL:";

    /**
     * attribute name for name of referenced application/library.
     */
    private static final String LIBRARY_NAME = "libraryName";

    /**
     * Update the given development component from the given '.dcdef' file.
     * 
     * @param component
     *            development component to update from given reader.
     * @param reader
     *            reader object for reading the '.dcdef' of the given component.
     */
    public void execute(final DevelopmentComponent component, final Reader reader) {
        try {
            final DigesterLoader digesterLoader = DigesterLoader.newLoader(new WebDynproProjectPropertiesModule());
            final Digester digester = digesterLoader.newDigester();
            digester.push(component);
            digester.parse(reader);
        }
        catch (final SAXException e) {
            throw new RuntimeException(e);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * rules module for parsing a <code>.dcdef</code> development component
     * configuration file.
     * 
     * @author Dirk Weigenand
     */
    class WebDynproProjectPropertiesModule extends AbstractRulesModule {
        @Override
        protected void configure() {
            addPublicPartCreationRule("ProjectProperties.LibraryReferences");
            addPublicPartCreationRule("ProjectProperties.SharingReferences");
        }

        /**
         * Add rule for public part creation from library and sharing
         * references.
         * 
         * @param elementName
         *            name of element to match.
         */
        private void addPublicPartCreationRule(final String elementName) {
            forPattern(String.format("ProjectProperties/%s/LibraryReference", elementName)).factoryCreate()
                .usingFactory(new PublicPartReferenceCreationFactory()).then().setNext("add");
        }
    }

    /**
     * Factory for creating {@link PublicPartReference} objects from a
     * <code>ProjectProperties.wdproperties</code> file.
     * 
     * @author Dirk Weigenand
     */
    class PublicPartReferenceCreationFactory extends AbstractObjectCreationFactory<PublicPartReference> {
        /**
         * Factory for {@link PublicPartReference} objects.
         */
        private final PublicPartReferenceFactory factory = new PublicPartReferenceFactory();

        /**
         * {@inheritDoc}
         * 
         * Create {@link PublicPartReference} objects from the
         * <code>libraryName</code> attribute of <code>LibraryReference</code>
         * elements.
         */
        @Override
        public PublicPartReference createObject(final Attributes attributes) throws Exception {
            return factory.create(attributes.getValue(LIBRARY_NAME).replace(PORTAL_PREFIX, ""));
        }
    }
}
