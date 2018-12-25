/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

/**
 * Types of development components.
 *
 * @author Dirk Weigenand
 */
public enum DevelopmentComponentType {
    /**
     * DC of type 'build infrastructure'.
     */
    BuildInfrastructure("Build Infrastructure", true),

    /**
     * DC of type 'Build Infrastructure Extension'.
     */
    BuildInfraStructureExtensions("Build Infrastructure Extension", true),

    /**
     * DC of type 'build plugin'.
     */
    BuildPlugin("Build Plugin", true),

    /**
     * DC of type 'dictionary'.
     */
    Dictionary("Dictionary", false),

    /**
     * DC of type 'portal standalone application'.
     */
    PortalApplicationStandalone("Enterprise Portal", "Portal Application Standalone", true),

    /**
     * DC of type 'portal application module'.
     */
    PortalApplicationModule("Enterprise Portal", "Portal Application Module", true),

    /**
     * DC of type 'external library'.
     */
    ExternalLibrary("External Library", false),

    /**
     * DC of type 'J2EE'.
     */
    J2EE("J2EE", false),

    /**
     * DC of type EJB module.
     */
    J2EEEjbModule("J2EE", "EJBModule", true),

    /**
     * DC of type 'J2EE server component'.
     */
    J2EEServerComponent("J2EE Server Component", false),

    /**
     * DC of type 'J2EE server component', subtype 'library'.
     */
    J2EEServerComponentLibrary("J2EE Server Component", "Library", false),

    /**
     * DC of type 'J2EE server component', subtype 'primary library'.
     */
    J2EEServerComponentPrimaryLibrary("J2EE Server Component", "Primary Library", false),

    /**
     * DC of type 'J2EE', subtype 'enterprise application'.
     */
    J2EEEnterpriseApplication("J2EE", "Enterprise Application", false),

    /**
     * DC of type 'J2EE Server Component', subtype 'service'.
     */
    J2EEServerComponentService("J2EE Server Component", "Service", false),

    /**
     * DC of type 'J2EE', subtype 'web module'.
     */
    J2EEWebModule("J2EE", "WebModule", true),

    /**
     * DC of type 'Software Component Description'.
     */
    SoftwareComponentDescription("Software Component Description", false),

    /**
     * DC of type 'java'.
     */
    Java("Java", true),

    /**
     * DC of type 'Web Dynpro'.
     */
    WebDynpro("Web Dynpro", true),

    /**
     * marks DC with no known type.
     */
    unknown("unknown", false),

    /**
     * DC of type 'web services', subtype 'deployable proxy'.
     */
    WebServicesDeployableProxy("Web Services", "Deployable Proxy", true),

    /**
     * UME permissions.
     */
    UMEPermissions("Content", "UME Permissions", false);

    /**
     * Allowed types of development component.
     */
    private static final Map<String, DevelopmentComponentType> TYPES = new HashMap<>();

    static {
        for (final DevelopmentComponentType dcType : values()) {
            TYPES.put(dcType.toString(), dcType);
        }
    }

    /**
     * type of development components.
     */
    private String type;

    /**
     * sub type of development components.
     */
    private String subType = "";

    /**
     * Indicate whether development components of this type can contain java source.
     */
    private boolean canContainJavaSources;

    /**
     * Create an instance of a <code>DevelopmentComponentType</code> with the given type. The sub type is empty (non existant).
     *
     * @param type
     *            the type of the development component.
     * @param canContainJavaSources
     *            indicate whether DCs of this type co.ntains java sources
     */
    DevelopmentComponentType(final String type, final boolean canContainJavaSources) {
        this.type = type;
        this.canContainJavaSources = canContainJavaSources;
    }

    /**
     * Create an instance of a <code>DevelopmentComponentType</code> with the given type and sub type.
     *
     * @param type
     *            the type of the development component.
     * @param subType
     *            the sub type of the development component.
     * @param canContainJavaSources
     *            indicate whether DCs of this type co.ntains java sources
     */
    DevelopmentComponentType(final String type, final String subType, final boolean canContainJavaSources) {
        this(type, canContainJavaSources);
        this.subType = subType;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(type);

        if (StringUtils.isNotEmpty(subType)) {
            result.append(':').append(subType);
        }

        return result.toString();
    }

    /**
     * Returns <code>true</code> when development components of this type can contain java source, <code>false</code> otherwise.
     *
     * @return <code>true</code> when development components of this type can contain java source, <code>false</code> otherwise.
     */
    public boolean canContainJavaSources() {
        return canContainJavaSources;
    }

    /**
     * factory method for creating development component types. If the given arguments do not match any type in this enum a
     * {@link DevelopmentComponentType#unknown} is returned.
     *
     * @param typeName
     *            name of the requested type of development components.
     * @param subTypeName
     *            name of the requested sub type of development components.
     * @return the matching development component type, {@link #unknown} otherwise.
     */
    public static DevelopmentComponentType fromString(final String typeName, final String subTypeName) {
        final String key = StringUtils.isEmpty(subTypeName) ? typeName : typeName + ':' + subTypeName;
        DevelopmentComponentType type = TYPES.get(key);

        if (type == null) {
            type = DevelopmentComponentType.unknown;
        }

        return type;
    }
}
