/**
 *
 */
package org.arachna.netweaver.dc.types;

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
    Jdk160Home("JDK1.6.0_HOME", "1.6"),

    /**
     * JDK 1.8.0 home path.
     */
    Jdk180Home("JDK1.8.0_HOME", "1.8");

    /**
     * Name of the alias.
     */
    private String alias;

    /**
     * Java source version.
     */
    private String sourceVersion;

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
     * @return the sourceVersion
     */
    public String getSourceVersion() {
        return sourceVersion;
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
        for (final JdkHomeAlias alias : values()) {
            if (alias.alias.equals(value)) {
                return alias;
            }
        }

        return null;
    }

    /**
     * Get the alias for the Java version Jenkins is running in.
     * 
     * @return the alias matching the Java version or {@link #Jdk180Home} when no
     *         match could be found (i.e. it's running Java 8 or higher).
     */
    public static JdkHomeAlias fromJavaVersion() {
        final String version = System.getProperty("java.version");

        for (final JdkHomeAlias alias : values()) {
            if (version.startsWith(alias.getSourceVersion())) {
                return alias;
            }
        }

        return Jdk180Home;
    }
}
