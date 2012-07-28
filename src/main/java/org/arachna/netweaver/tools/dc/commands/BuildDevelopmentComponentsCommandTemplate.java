/**
 * 
 */
package org.arachna.netweaver.tools.dc.commands;

import java.util.HashMap;
import java.util.Map;

import org.arachna.netweaver.dc.types.JdkHomeAlias;

/**
 * @author Dirk Weigenand (G526521)
 * 
 */
enum BuildDevelopmentComponentsCommandTemplate {
    /**
     * template for pre NetWeaver CE systems.
     */
    V70("builddc -s %s -n %s -v %s -o;"),

    /**
     * template for NetWeaver CE or post CE systems.
     */
    V71("builddc -c %s -n %s -v %s");

    private static final Map<JdkHomeAlias, BuildDevelopmentComponentsCommandTemplate> TEMPLATES =
        new HashMap<JdkHomeAlias, BuildDevelopmentComponentsCommandTemplate>() {
            {
                put(JdkHomeAlias.Jdk131Home, V70);
                put(JdkHomeAlias.Jdk142Home, V70);
                put(JdkHomeAlias.Jdk150Home, V71);
                put(JdkHomeAlias.Jdk160Home, V71);
            }
        };

    /**
     * Template for issuing build dc commands.
     */
    private final String buildDcCommand;

    BuildDevelopmentComponentsCommandTemplate(String buildDcCommand) {
        this.buildDcCommand = buildDcCommand;
    }

    /**
     * Create a template for synchronizing development components.
     * 
     * @param alias
     *            Alias for JDK_HOME.
     * @return template corresponding to the given alias.
     */
    public static final BuildDevelopmentComponentsCommandTemplate create(JdkHomeAlias alias) {
        BuildDevelopmentComponentsCommandTemplate template = TEMPLATES.get(alias);

        if (template == null) {
            throw new IllegalStateException(String.format("Could not map SyncDcCommandTemplate onto %s!", alias));
        }

        return template;
    }
}
