/**
 * 
 */
package org.arachna.netweaver.dctool.commands;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.HashSet;
import java.util.Set;

import org.arachna.netweaver.dc.types.Compartment;

/**
 * Templates for syncing/unsyncing single/all DCs from a compartment.
 * 
 * @author Dirk Weigenand
 */
public enum SyncDcCommandTemplate {
    SyncDcCommandTemplateV70("syncdc -s %s -n %s -v %s -m inactive -y;", "syncalldcs -s %s -m archive;",
        "unsyncdc -s %s -n %s -v %s;", "exit;", "SoftwareComponents70"), SyncDcCommandTemplateV71(
        "syncdc -c %s -n %s -v %s -m inactive -f", "syncalldcs -c %s -m archive", "unsyncdc -c %s -n %s -v %s", "exit",
        "SoftwareComponents73");

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
     * 
     */
    private Set<String> excludeSCs = new HashSet<String>();

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
    SyncDcCommandTemplate(final String syncInactiveDcTemplate, final String syncAllDcsInArchiveModeTemplate,
        final String unsyncDcTemplate, final String exitTemplate, final String compartmentDirectory) {
        this.syncInactiveDcTemplate = syncInactiveDcTemplate;
        this.syncAllDcsInArchiveModeTemplate = syncAllDcsInArchiveModeTemplate;
        this.unsyncDcTemplate = unsyncDcTemplate;
        this.exitTemplate = exitTemplate;

        try {
            this.excludeSCs.addAll(new CompartmentsReader().read("/org/arachna/netweaver/dctool/commands/"
                + compartmentDirectory));
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    /**
     * Returns whether the given compartment should be excluded from
     * synchronization.
     * 
     * @param compartment
     * @return
     */
    public boolean shouldCompartmentBeExcludedFromSynchronization(Compartment compartment) {
        return this.excludeSCs.contains(compartment.getName());
    }

    static final class CompartmentsReader {
        Set<String> read(String compartmentDirectory) throws IOException {
            Set<String> compartments = new HashSet<String>();

            LineNumberReader reader =
                new LineNumberReader(new InputStreamReader(this.getClass().getResourceAsStream(compartmentDirectory)));
            String compartment;
            while ((compartment = reader.readLine()) != null) {
                compartments.add(String.format("sap.com_%s_1", compartment.trim()));
            }

            return compartments;
        }
    }
}
