/**
 *
 */
package org.arachna.netweaver.dc.types;

import java.util.HashMap;
import java.util.Map;

/**
 * Aliases for JDK homes in NWDI.
 * 
 * @author Dirk Weigenand
 */
public enum JdkHomeAlias {
    /**
     * JDK 1.3.1 home path.
     */
    Jdk131Home("JDK1.3.1_HOME", "1.3"),

    /**
     * JDK 1.4.2 home path.
     */
    Jdk142Home("JDK1.4.2_HOME", "1.4"),

    /**
     * JDK 1.5.0 home path.
     */
    Jdk150Home("JDK1.5.0_HOME", "1.5"),

    /**
     * JDK 1.6.0 home path.
     */
    Jdk160Home("JDK1.6.0_HOME", "1.6");

    /**
     * Aliases for JDK homes in NWDI.
     */
    private static final Map<String, JdkHomeAlias> ALIASES = new HashMap<String, JdkHomeAlias>();

    static {
        for (final JdkHomeAlias alias : values()) {
            ALIASES.put(alias.toString(), alias);
        }
    }

    /**
     * Name of the alias.
     */
    private String alias;

    /**
     * Java source version.
     */
    private String sourceVersion;

    /**
     * @return the sourceVersion
     */
    public String getSourceVersion() {
        return sourceVersion;
    }

    /**
     * Alias for a JDK installation.
     * 
     * @param alias
     *            Name of the alias.
     * @param sourceVersion
     *            Version of java source to compile with
     */
    JdkHomeAlias(final String alias, final String sourceVersion) {
        this.alias = alias;
        this.sourceVersion = sourceVersion;
    }

    /**
     * Name of the alias.
     * 
     * @return name of the alias.
     */
    @Override
    public String toString() {
        return alias;
    }

    /**
     * Get the alias for the given string.
     * 
     * @param value
     *            name of the alias.
     * @return the alias found or <code>null</code>.
     */
    public static JdkHomeAlias fromString(final String value) {
        return ALIASES.get(value);
    }
}
