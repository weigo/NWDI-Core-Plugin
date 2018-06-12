/**
 *
 */
package org.arachna.netweaver.tools.dc;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.JdkHomeAlias;

/**
 * Templates for syncing/unsyncing single/all DCs from a compartment.
 *
 * @author Dirk Weigenand
 */
enum SyncDcCommandTemplate {
    /**
     * Template for NW 7.0.
     */
    V70(
        "syncdc -s %s -n %s -v %s -m inactive -y;",
        "syncdc -s %s -n %s -v %s -m archive --syncused;",
        "syncalldcs -s %s -m archive;",
        "syncalldcs -s %s -m inactive;",
        "unsyncdc -s %s -n %s -v %s;"),

    /**
     * Template for NW 7.1+.
     */
    V71(
        "syncdc -c %s -n %s -v %s -m inactive -f",
        "syncdc -c %s -n %s -v %s -m archive",
        "syncalldcs -c %s -m archive",
        "syncalldcs -c %s -m inactive",
        "unsyncdc -c %s -n %s -v %s");

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
     * Create an instance of a command template provider using the given templates.
     *
     * @param syncInactiveDcTemplate
     *            template to use for creating a 'syncdc' in inactive state command.
     * @param syncArchiveDcTemplate
     *            template to use for creating a 'syncdc' in active state command.
     * @param syncAllDcsForGivenCompartmentInArchiveModeTemplate
     *            template to use for creating a 'syncalldcs' in active state command for a given compartment.
     * @param syncAllDcsInInactiveModeTemplate
     *            template for creating a 'syncalldcs' in archive mode command.
     * @param unsyncDcTemplate
     *            template to use for creating a 'unsyncdc' in active state command.
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
     * Create a template for synchronizing development components.
     *
     * @param alias
     *            Alias for JDK_HOME.
     * @return template corresponding to the given alias.
     */
    public static final SyncDcCommandTemplate create(final JdkHomeAlias alias) {
        SyncDcCommandTemplate template = null;

        switch (alias) {
            case Jdk131Home:
            case Jdk142Home:
                template = V70;
                break;

            case Jdk150Home:
            case Jdk160Home:
            case Jdk180Home:
                template = V71;
                break;

            default:
                throw new IllegalStateException(String.format("Could not map SyncDcCommandTemplate onto %s!", alias));
        }

        return template;
    }

    /**
     * Create command for synchronizing DCs in source state.
     *
     * @param compartment
     *            the compartment whose components should be synchronized in source state.
     * @return command for synchronizing DCs in source state.
     */
    String createSyncDcsInInActiveModeCommand(final Compartment compartment) {
        return String.format(syncAllDcsInInactiveModeTemplate, compartment.getName());
    }

    /**
     * Create command to unsynchronize the given DC.
     *
     * @param component
     *            DC to unsynchronize.
     * @return dctool command to unsynchronize the given DC
     */
    String createUnsyncDCCommand(final DevelopmentComponent component) {
        return String.format(unsyncDcTemplate, component.getCompartment().getName(), component.getName(), component.getVendor());
    }

    /**
     * Create command for synchronizing the given DC in inactive state.
     *
     * @param component
     *            DC to synchronize.
     * @return dctool command to synchronize the given DC
     */
    String createSyncInactiveDCCommand(final DevelopmentComponent component) {
        return String.format(syncInactiveDcTemplate, component.getCompartment().getName(), component.getName(), component.getVendor());
    }

    /**
     * Create command for synchronizing the given DC in inactive state.
     *
     * @param component
     *            DC to synchronize.
     * @return dctool command to synchronize the given DC
     */
    String createSyncArchiveDCCommand(final DevelopmentComponent component) {
        return String.format(syncArchiveDcTemplate, component.getCompartment().getName(), component.getName(), component.getVendor());
    }

    /**
     * Create command for synchronizing all components in the given compartment.
     *
     * @param compartment
     *            compartment to synchronize.
     * @return synchronize compartment command.
     */
    String createSyncCompartmentInArchiveModeCommand(final Compartment compartment) {
        return String.format(syncAllDcsForGivenCompartmentInArchiveModeTemplate, compartment.getName());
    }
}
