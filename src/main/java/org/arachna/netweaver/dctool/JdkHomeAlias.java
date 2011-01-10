/**
 *
 */
package org.arachna.netweaver.dctool;

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
    Jdk131Home("JDK1.3.1_HOME"),

    /**
     * JDK 1.4.2 home path.
     */
    Jdk142Home("JDK1.4.2_HOME");

    /**
     * Aliases for JDK homes in NWDI.
     */
    private static final Map<String, JdkHomeAlias> ALIASES = new HashMap<String, JdkHomeAlias>();

    static {
        for (JdkHomeAlias alias : values()) {
            ALIASES.put(alias.toString(), alias);
        }
    }

    /**
     * Name of the alias.
     */
    private String alias;

    /**
     * Alias for a JDK installation.
     * 
     * @param alias
     *            Name of the alias.
     */
    JdkHomeAlias(final String alias) {
        this.alias = alias;
    }

    /**
     * Name of the alias.
     * 
     * @return name of the alias.
     */
    @Override
    public String toString() {
        return this.alias;
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
