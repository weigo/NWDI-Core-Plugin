/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.util.HashMap;
import java.util.Map;

/**
 * Types of development components.
 * 
 * @author Dirk Weigenand
 */
public enum DevelopmentComponentType {
    /**
     * Initializers.
     */
    /**
     * DC of type 'build infrastructure'.
     */
    BuildInfrastructure("Build Infrastructure"),
    /**
     * DC of type 'build plugin'.
     */
    BuildPlugin("Build Plugin"),
    /**
     * DC of type 'dictionary'.
     */
    Dictionary("Dictionary"),
    /**
     * DC of type 'portal standalone application'.
     */
    PortalApplicationStandalone("Enterprise Portal", "Portal Application Standalone"),
    /**
     * DC of type 'portal application module'.
     */
    PortalApplicationModule("Enterprise Portal", "Portal Application Module"),
    /**
     * DC of type 'external library'.
     */
    ExternalLibrary("External Library"),
    /**
     * DC of type 'J2EE'.
     */
    J2EE("J2EE"),
    /**
     * DC of type 'J2EE server component'.
     */
    J2EEServerComponent("J2EE Server Component"),
    /**
     * DC of type 'J2EE server component', subtype 'library'.
     */
    J2EEServerComponentLibrary("J2EE Server Component", "Library"),
    /**
     * DC of type 'J2EE', subtype 'enterprise application'.
     */
    J2EEEnterpriseApplication("J2EE", "Enterprise Application"),
    /**
     * DC of type 'J2EE', subtype 'web module'.
     */
    J2EEWebModule("J2EE", "WebModule"),
    /**
     * DC of type 'java'.
     */
    Java("Java"),
    /**
     * DC of type 'Web Dynpro'.
     */
    WebDynpro("Web Dynpro"),
    /**
     * marks DC with no known type.
     */
    unknown("unknown"),
    /**
     * DC of type 'web services', subtype 'deployable proxy'.
     */
    WebServicesDeployableProxy("Web Services", "Deployable Proxy");

    /**
     * Allowed types of development component.
     */
    private static final Map<String, DevelopmentComponentType> TYPES = new HashMap<String, DevelopmentComponentType>();

    static {
        for (final DevelopmentComponentType type : values()) {
            TYPES.put(type.toString(), type);
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
     * Create an instance of a <code>DevelopmentComponentType</code> with the
     * given type. The sub type is empty (non existant).
     * 
     * @param type
     *            the type of the development component.
     */
    DevelopmentComponentType(final String type) {
        this.type = type;
    }

    /**
     * Create an instance of a <code>DevelopmentComponentType</code> with the
     * given type and sub type.
     * 
     * @param type
     *            the type of the development component.
     * @param subType
     *            the sub type of the development component.
     */
    DevelopmentComponentType(final String type, final String subType) {
        this(type);
        this.subType = subType;
    }

    @Override
    public String toString() {
        final StringBuilder result = new StringBuilder(this.type);

        if (this.subType.trim().length() > 0) {
            result.append(':').append(this.subType);
        }

        return result.toString();
    }

    /**
     * factory method for creating development component types. If the given
     * arguments do not match any type in this enum a
     * {@link DevelopmentComponentType#unknown} is returned.
     * 
     * @param typeName
     *            name of the requested type of development components.
     * @param subTypeName
     *            name of the requested sub type of development components.
     * @return the matching development component type, {@link #unknown}
     *         otherwise.
     */
    public static DevelopmentComponentType fromString(final String typeName, final String subTypeName) {
        final String key =
            subTypeName == null || subTypeName.trim().length() == 0 ? typeName : typeName + ':' + subTypeName;
        DevelopmentComponentType type = TYPES.get(key);

        if (type == null) {
            type = DevelopmentComponentType.unknown;
        }

        return type;
    }
}
