/**
 * 
 */
package org.arachna.netweaver.dc.types;

/**
 * Build option objects.
 * 
 * @author Dirk Weigenand
 */
public class BuildOption {
    /**
     * build option name.
     */
    private final String name;

    /**
     * Value of build option.
     */
    private String value;

    /**
     * Create new build option with name.
     * 
     * @param name
     *            name of build option.
     */
    public BuildOption(final String name) {
        this.name = name;
    }

    /**
     * Create new build option with name and value.
     * 
     * @param name
     *            name of build option.
     * @param value
     *            the value of the build option.
     */
    public BuildOption(final String name, final String value) {
        this(name);
        this.value = value;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(final String value) {
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
}