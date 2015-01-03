/**
 *
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.xml.RulesModuleProducer;
import org.xml.sax.Attributes;

/**
 * Read a portal applications 'portalapp.xml' configuration file and extract
 * information on its sharing references.
 * 
 * @author Dirk Weigenand
 */
final class PortalApplicationConfigurationRulesModuleProducer implements RulesModuleProducer {
    /**
     * constant for attribute 'value'.
     */
    private static final String VALUE = "value";

    /**
     * constant for attribute 'name'.
     */
    private static final String NAME = "name";

    /**
     * constant for attribute 'SharingReference'.
     */
    private static final String SHARING_REFERENCE_ATTRIBUTE_VALUE = "SharingReference";

    /**
     * constant for attribute 'SharingReference'.
     */
    private static final String PRIVATE_SHARING_REFERENCE_ATTRIBUTE_VALUE = "PrivateSharingReference";

    /**
     * Create a rules module for parsing a <code>portalapp.xml</code>
     * development component configuration file.
     * 
     * @return a rules module for parsing a <code>portalapp.xml</code>
     *         configuration file.
     */
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("application/application-config/property").factoryCreate()
                    .usingFactory(new PublicPartReferenceCreationFactory()).then().setNext("addAll");
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
        AbstractObjectCreationFactory<Collection<PublicPartReference>> {
        /**
         * Factory for {@link PublicPartReference} objects.
         */
        private final PublicPartReferenceFactory factory = new PublicPartReferenceFactory();

        /**
         * {@inheritDoc}
         * 
         * Create {@link PublicPartReference} objects from the
         * <code>SharingReference</code>s.
         */
        @Override
        public Collection<PublicPartReference> createObject(final Attributes attributes) throws Exception {
            if (hasSharingReferenceAttribute(attributes)) {
                final Collection<PublicPartReference> references = new LinkedList<PublicPartReference>();

                for (final String sharingReference : attributes.getValue(VALUE).split(",\\s*")) {
                    final PublicPartReference publicPartReference =
                        factory.create(SharingReferencePrefix.getReference(sharingReference));
                    publicPartReference.setAtRunTime();

                    references.add(publicPartReference);
                }

                return references;
            }

            return Collections.emptyList();
        }

        /**
         * Determine whether the property name attribute describes a
         * <code>SharingReference</code> or <code>PrivateSharingReference</code>
         * .
         * 
         * @param attributes
         *            SAX attributes of an application property config element.
         * @return <code>true</code> when the property name attribute has the
         *         value {@see #PortalApplicationConfigurationReader.
         *         PRIVATE_SHARING_REFERENCE_ATTRIBUTE_VALUE} or {@see
         *         #PortalApplicationConfigurationReader.
         *         SHARING_REFERENCE_ATTRIBUTE_VALUE}
         */
        private boolean hasSharingReferenceAttribute(final Attributes attributes) {
            final String nameAttribute = attributes.getValue(NAME);

            return nameAttribute != null
                && (SHARING_REFERENCE_ATTRIBUTE_VALUE.equals(nameAttribute) || PRIVATE_SHARING_REFERENCE_ATTRIBUTE_VALUE
                    .equals(nameAttribute));
        }
    }
}