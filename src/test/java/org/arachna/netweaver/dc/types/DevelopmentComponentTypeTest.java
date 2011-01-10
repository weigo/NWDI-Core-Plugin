package org.arachna.netweaver.dc.types;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Unit Tests for {@link org.arachna.netweaver.dc.types.DevelopmentComponentType}
 * 
 * @author Dirk Weigenand
 */
public class DevelopmentComponentTypeTest {

    /**
     * Assert that creating a development component type from empty strings returns {@link DevelopmentComponentType#unknown}.
     */
    @Test
    public final void testGetUnknownDevelopmentComponentTypeFromEmptyString() {
        assertEquals(DevelopmentComponentType.unknown, DevelopmentComponentType.fromString("", ""));
    }

    /**
     * Assert that creating a development component type from a mix of empty strings and null values returns
     * {@link DevelopmentComponentType#unknown}.
     */
    @Test
    public final void testGetUnknownDevelopmentComponentTypeFromNullString() {
        assertEquals(DevelopmentComponentType.unknown, DevelopmentComponentType.fromString("", null));
    }

    /**
     * Assert that creating a development component type from a mix of empty strings and null values returns
     * {@link DevelopmentComponentType#unknown}.
     */
    @Test
    public final void testGetDevelopmentComponentTypeFrom() {
        assertEquals(DevelopmentComponentType.unknown, DevelopmentComponentType.fromString(null, ""));
    }

    /**
     * Assert that creating a development component type from null values returns {@link DevelopmentComponentType#unknown}.
     */
    @Test
    public final void testGetUnknownDevelopmentComponentTypeFromNullComponentTypeAndNullSubTypeString() {
        assertEquals(DevelopmentComponentType.unknown, DevelopmentComponentType.fromString(null, null));
    }

    /**
     * Assert creating the correct development component type for a deployable WebService proxy.
     */
    @Test
    public final void testGetWebServicesDeployableProxyFromString() {
        assertEquals(DevelopmentComponentType.WebServicesDeployableProxy,
                DevelopmentComponentType.fromString("Web Services", "Deployable Proxy"));
    }

    /**
     * Assert creating the correct development component type for a WebDynpro DC.
     */

    @Test
    public final void testGetWebDynproFromString() {
        assertEquals(DevelopmentComponentType.WebDynpro, DevelopmentComponentType.fromString("Web Dynpro", ""));
    }

}
