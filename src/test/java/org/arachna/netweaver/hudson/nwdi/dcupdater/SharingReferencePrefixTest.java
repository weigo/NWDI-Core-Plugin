/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi.dcupdater;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Unit tests for {@link SharingReferencePrefix}.
 * 
 * @author Dirk Weigenand
 */
public class SharingReferencePrefixTest {
    private static final String EXAMPLE_REFERENCE = "org.example.dc1";

    @Test
    public void assertCorrectApplicationReference() {
        assertThat(SharingReferencePrefix.getReference(EXAMPLE_REFERENCE), equalTo(EXAMPLE_REFERENCE));
    }

    @Test
    public void assertCorrectServiceReference() {
        final String serviceReference = SharingReferencePrefix.Service.getPrefix() + EXAMPLE_REFERENCE;
        assertThat(SharingReferencePrefix.getReference(serviceReference), equalTo(EXAMPLE_REFERENCE));
    }

    @Test
    public void assertCorrectInterfaceReference() {
        final String interfaceReference = SharingReferencePrefix.Interface.getPrefix() + EXAMPLE_REFERENCE;
        assertThat(SharingReferencePrefix.getReference(interfaceReference), equalTo(EXAMPLE_REFERENCE));
    }

    @Test
    public void assertCorrectLibraryReference() {
        final String libraryReference = SharingReferencePrefix.Library.getPrefix() + EXAMPLE_REFERENCE;
        assertThat(SharingReferencePrefix.getReference(libraryReference), equalTo(EXAMPLE_REFERENCE));
    }
}
