/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.xml.RulesModuleProducer;
import org.xml.sax.Attributes;

/**
 * Reader for <code>ProjectProperties.wdproperties</code> configuration files.
 * 
 * @author Dirk Weigenand
 */
final class WebDynproProjectPropertiesRulesModuleProducer implements RulesModuleProducer {
    /**
     * prefix used to reference portal applications/services.
     */
    private static final String PORTAL_PREFIX = "PORTAL:";

    /**
     * attribute name for name of referenced application/library.
     */
    private static final String LIBRARY_NAME = "libraryName";

    /**
     * Create a rules module for parsing a
     * <code>ProjectProperties.wdproperties</code> development component
     * configuration file.
     * 
     * @return a rules module for parsing WebDynPro properties (pre NetWeaver
     *         CE).
     * @author Dirk Weigenand
     */
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
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
        };
    }

    /**
     * Factory for creating {@link PublicPartReference} objects from a
     * <code>ProjectProperties.wdproperties</code> file.
     * 
     * @author Dirk Weigenand
     */
    private static final class PublicPartReferenceCreationFactory extends
        AbstractObjectCreationFactory<PublicPartReference> {
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
