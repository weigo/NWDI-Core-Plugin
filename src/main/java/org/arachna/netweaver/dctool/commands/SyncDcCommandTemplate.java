/**
 * 
 */
package org.arachna.netweaver.dctool.commands;

/**
 * Templates for syncing/unsyncing single/all DCs from a compartment.
 * 
 * @author Dirk Weigenand
 */
public enum SyncDcCommandTemplate {
    SyncDcCommandTemplateV70("syncdc -s %s -n %s -v %s -m inactive -y;", "syncalldcs -s %s -m archive;",
        "unsyncdc -s %s -n %s -v %s;", "exit;"), SyncDcCommandTemplateV71("syncdc -c %s -n %s -v %s -m inactive -f",
        "syncalldcs -c %s -m archive", "unsyncdc -c %s -n %s -v %s", "exit");

    /**
     * template to use for creating a 'syncdc' in inactive state command.
     */
    private String syncInactiveDcTemplate;

    /**
     * template to use for creating a 'syncalldcs' in active state command.
     */
    private String syncAllDcsInArchiveModeTemplate;

    /**
     * template to use for creating a 'unsyncdc' in active state command.
     */
    private String unsyncDcTemplate;

    /**
     * template for generating an 'exit' command.
     */
    private String exitTemplate;

    /**
     * Create an instance of a command template provider using the given
     * templates.
     * 
     * @param syncInactiveDcTemplate
     *            template to use for creating a 'syncdc' in inactive state
     *            command.
     * @param syncAllDcsInArchiveModeTemplate
     *            template to use for creating a 'syncalldcs' in active state
     *            command.
     * @param unsyncDcTemplate
     *            template to use for creating a 'unsyncdc' in active state
     *            command.
     * @param exit
     *            template for generating an 'exit' command.
     */
    SyncDcCommandTemplate(String syncInactiveDcTemplate, String syncAllDcsInArchiveModeTemplate,
        String unsyncDcTemplate, String exitTemplate) {
        this.syncInactiveDcTemplate = syncInactiveDcTemplate;
        this.syncAllDcsInArchiveModeTemplate = syncAllDcsInArchiveModeTemplate;
        this.unsyncDcTemplate = unsyncDcTemplate;
        this.exitTemplate = exitTemplate;
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
     * Returns template to use for creating a 'syncalldcs' in active state
     * command.
     * 
     * @return template to use for creating a 'syncalldcs' in active state
     *         command.
     */
    String getSyncAllDcsInArchiveModeTemplate() {
        return syncAllDcsInArchiveModeTemplate;
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
     * @return the exitTemplate
     */
    String getExitTemplate() {
        return exitTemplate;
    }
}
