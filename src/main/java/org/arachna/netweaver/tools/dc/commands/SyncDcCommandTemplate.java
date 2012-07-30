/**
 * 
 */
package org.arachna.netweaver.tools.dc.commands;

import java.util.HashMap;
import java.util.Map;

import org.arachna.netweaver.dc.types.JdkHomeAlias;

/**
 * Templates for syncing/unsyncing single/all DCs from a compartment.
 * 
 * @author Dirk Weigenand
 */
public enum SyncDcCommandTemplate {
    /**
     * Template for NW 7.0.
     */
    V70("syncdc -s %s -n %s -v %s -m inactive -y;", "syncdc -s %s -n %s -v %s -m archive -u;",
        "syncalldcs -s %s -m archive;", "syncalldcs -s %s -m inactive;", "unsyncdc -s %s -n %s -v %s;"),

    /**
     * Template for NW 7.1+.
     */
    V71("syncdc -c %s -n %s -v %s -m inactive -f", "syncdc -c %s -n %s -v %s -m archive",
        "syncalldcs -c %s -m archive", "syncalldcs -c %s -m inactive", "unsyncdc -c %s -n %s -v %s");

    /**
     * mapping from JdkHomeAlias to template used for generating DC tool
     * commands for synchronizing development components.
     */
    private static final Map<JdkHomeAlias, SyncDcCommandTemplate> TEMPLATES =
        new HashMap<JdkHomeAlias, SyncDcCommandTemplate>() {
            {
                put(JdkHomeAlias.Jdk131Home, V70);
                put(JdkHomeAlias.Jdk142Home, V70);
                put(JdkHomeAlias.Jdk150Home, V71);
                put(JdkHomeAlias.Jdk160Home, V71);
            }
        };

    /**
     * template to use for creating a 'syncdc' in inactive state command.
     */
    private String syncInactiveDcTemplate;

    /**
     * template to use for creating a 'syncdc' in active state command.
     */
    private String syncArchiveDcTemplate;

    /**
     * template to use for creating a 'syncalldcs' in archive state command.
     */
    private String syncAllDcsForGivenCompartmentInArchiveModeTemplate;

    /**
     * template to use for creating a 'syncalldcs' in inactive state command.
     */
    private String syncAllDcsInInactiveModeTemplate;

    /**
     * template to use for creating a 'unsyncdc' in active state command.
     */
    private String unsyncDcTemplate;

    /**
     * Create an instance of a command template provider using the given
     * templates.
     * 
     * @param syncInactiveDcTemplate
     *            template to use for creating a 'syncdc' in inactive state
     *            command.
     * @param syncArchiveDcTemplate
     *            template to use for creating a 'syncdc' in active state
     *            command.
     * @param syncAllDcsForGivenCompartmentInArchiveModeTemplate
     *            template to use for creating a 'syncalldcs' in active state
     *            command for a given compartment.
     * @param syncAllDcsInInactiveModeTemplate
     *            template for creating a 'syncalldcs' in archive mode command.
     * @param unsyncDcTemplate
     *            template to use for creating a 'unsyncdc' in active state
     *            command.
     */
    SyncDcCommandTemplate(final String syncInactiveDcTemplate, final String syncArchiveDcTemplate,
        final String syncAllDcsForGivenCompartmentInArchiveModeTemplate, final String syncAllDcsInInactiveModeTemplate,
        final String unsyncDcTemplate) {
        this.syncInactiveDcTemplate = syncInactiveDcTemplate;
        this.syncArchiveDcTemplate = syncArchiveDcTemplate;
        this.syncAllDcsForGivenCompartmentInArchiveModeTemplate = syncAllDcsForGivenCompartmentInArchiveModeTemplate;
        this.syncAllDcsInInactiveModeTemplate = syncAllDcsInInactiveModeTemplate;
        this.unsyncDcTemplate = unsyncDcTemplate;
    }

    /**
     * Returns template to use for creating a 'syncdc' in inactive state
     * command.
     * 
     * @return template to use for creating a 'syncdc' in inactive state
     *         command.
     */
    String getSyncInactiveDcTemplate() {
        return syncInactiveDcTemplate;
    }

    /**
     * Returns template to use for creating a 'syncdc' in active state command.
     * 
     * @return template to use for creating a 'syncdc' in active state command.
     */
    String getSyncArchiveDcTemplate() {
        return syncArchiveDcTemplate;
    }

    /**
     * Returns template to use for creating a 'syncalldcs' in active state
     * command for a given compartment.
     * 
     * @return template to use for creating a 'syncalldcs' in active state
     *         command for a given compartment.
     */
    String getSyncAllDcsForGivenCompartmentInArchiveModeTemplate() {
        return syncAllDcsForGivenCompartmentInArchiveModeTemplate;
    }

    /**
     * Returns the template to create a 'syncalldcs' command for synchronizing
     * DCs in inactive mode.
     * 
     * @return template to create a 'syncalldcs' command for synchronizing DCs
     *         in inactive mode.
     */
    String getSyncAllDcsInInactiveModeTemplate() {
        return syncAllDcsInInactiveModeTemplate;
    }

    /**
     * Returns template to use for creating a 'unsyncdc' in active state
     * command.
     * 
     * @return template to use for creating a 'unsyncdc' in active state
     *         command.
     */
    String getUnsyncDcTemplate() {
        return unsyncDcTemplate;
    }

    /**
     * Create a template for synchronizing development components.
     * 
     * @param alias
     *            Alias for JDK_HOME.
     * @return template corresponding to the given alias.
     */
    public static final SyncDcCommandTemplate create(final JdkHomeAlias alias) {
        final SyncDcCommandTemplate template = TEMPLATES.get(alias);

        if (template == null) {
            throw new IllegalStateException(String.format("Could not map SyncDcCommandTemplate onto %s!", alias));
        }

        return template;
    }
}
