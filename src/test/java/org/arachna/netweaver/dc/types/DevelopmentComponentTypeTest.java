package org.arachna.netweaver.dc.types;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * Unit Tests for {@link DevelopmentComponentType}.
 * 
 * @author Dirk Weigenand
 */
public class DevelopmentComponentTypeTest {

    /**
     * Assert that creating a development component type from empty strings returns {@link DevelopmentComponentType#unknown}.
     */
    @Test
    public final void testGetUnknownDevelopmentComponentTypeFromEmptyString() {
        assertThat(DevelopmentComponentType.unknown, equalTo(DevelopmentComponentType.fromString("", "")));
    }

    /**
     * Assert that creating a development component type from a mix of empty strings and null values returns
     * {@link DevelopmentComponentType#unknown}.
     */
    @Test
    public final void testGetUnknownDevelopmentComponentTypeFromNullString() {
        assertThat(DevelopmentComponentType.unknown, equalTo(DevelopmentComponentType.fromString("", null)));
    }

    /**
     * Assert that creating a development component type from a mix of empty strings and null values returns
     * {@link DevelopmentComponentType#unknown}.
     */
    @Test
    public final void testGetDevelopmentComponentTypeFrom() {
        assertThat(DevelopmentComponentType.unknown, equalTo(DevelopmentComponentType.fromString(null, "")));
    }

    /**
     * Assert that creating a development component type from null values returns {@link DevelopmentComponentType#unknown}.
     */
    @Test
    public final void testGetUnknownDevelopmentComponentTypeFromNullComponentTypeAndNullSubTypeString() {
        assertThat(DevelopmentComponentType.unknown, equalTo(DevelopmentComponentType.fromString(null, null)));
    }

    /**
     * Assert creating the correct development component type for a deployable WebService proxy.
     */
    @Test
    public final void testGetWebServicesDeployableProxyFromString() {
        assertThat(DevelopmentComponentType.WebServicesDeployableProxy,
            equalTo(DevelopmentComponentType.fromString("Web Services", "Deployable Proxy")));
    }

    /**
     * Assert creating the correct development component type for a WebDynpro DC.
     */
    @Test
    public final void testGetWebDynproFromString() {
        assertThat(DevelopmentComponentType.WebDynpro, equalTo(DevelopmentComponentType.fromString("Web Dynpro", "")));
    }

    /**
     * Assert the development component type unknown cannot contain java sources.
     */
    @Test
    public void testUnknownDCsCanContainJavaSources() {
        assertThat(DevelopmentComponentType.unknown.canContainJavaSources(), equalTo(false));
    }
}
