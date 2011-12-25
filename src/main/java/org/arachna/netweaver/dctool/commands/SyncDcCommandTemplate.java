/**
 * 
 */
package org.arachna.netweaver.dctool.commands;

import java.io.IOException;
import java.io.InputStream;
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
    /**
     * Template for NW 7.0.
     */
    SyncDcCommandTemplateV70("syncdc -s %s -n %s -v %s -m inactive -y;", "syncalldcs -s %s -m archive;",
        "syncalldcs -m archive;", "syncalldcs -s %s -m inactive;", "unsyncdc -s %s -n %s -v %s;", "exit;",
        "SoftwareComponents70"),

    /**
     * Template for NW 7.1+.
     */
    SyncDcCommandTemplateV71("syncdc -c %s -n %s -v %s -m inactive -f", "syncalldcs -c %s -m archive",
        "syncalldcs -m archive", "syncalldcs -c %s -m inactive", "unsyncdc -c %s -n %s -v %s", "exit",
        "SoftwareComponents73");

    /**
     * template to use for creating a 'syncdc' in inactive state command.
     */
    private String syncInactiveDcTemplate;

    /**
     * template to use for creating a 'syncalldcs' in archive state command.
     */
    private String syncAllDcsForGivenCompartmentInArchiveModeTemplate;

    /**
     * Template for synchronizing all DCs of a development configuration in
     * archive mode.
     */
    private final String syncAllDcsInArchiveModeTemplate;

    /**
     * template to use for creating a 'syncalldcs' in inactive state command.
     */
    private String syncAllDcsInInactiveModeTemplate;

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
    private final Set<String> excludeSCs = new HashSet<String>();

    /**
     * Create an instance of a command template provider using the given
     * templates.
     * 
     * @param syncInactiveDcTemplate
     *            template to use for creating a 'syncdc' in inactive state
     *            command.
     * @param syncAllDcsForGivenCompartmentInArchiveModeTemplate
     *            template to use for creating a 'syncalldcs' in active state
     *            command for a given compartment.
     * @param syncAllDcsInArchiveModeTemplate
     *            template to use for creating a 'syncalldcs' in active state
     *            command for a whole development configuration.
     * @param syncAllDcsInInactiveModeTemplate
     *            template for creating a 'syncalldcs' in archive mode command.
     * @param unsyncDcTemplate
     *            template to use for creating a 'unsyncdc' in active state
     *            command.
     * @param exitTemplate
     *            template for generating an 'exit' command.
     * @param compartmentDirectory
     *            the 'compartment directory' containing software components
     *            supplied for the respective NW version.
     * @param syncAllDcsInArchiveModeTemplate
     */
    SyncDcCommandTemplate(final String syncInactiveDcTemplate,
        final String syncAllDcsForGivenCompartmentInArchiveModeTemplate, final String syncAllDcsInArchiveModeTemplate,
        final String syncAllDcsInInactiveModeTemplate, final String unsyncDcTemplate, final String exitTemplate,
        final String compartmentDirectory) {
        this.syncInactiveDcTemplate = syncInactiveDcTemplate;
        this.syncAllDcsForGivenCompartmentInArchiveModeTemplate = syncAllDcsForGivenCompartmentInArchiveModeTemplate;
        this.syncAllDcsInArchiveModeTemplate = syncAllDcsInArchiveModeTemplate;
        this.syncAllDcsInInactiveModeTemplate = syncAllDcsInInactiveModeTemplate;
        this.unsyncDcTemplate = unsyncDcTemplate;
        this.exitTemplate = exitTemplate;

        try {
            excludeSCs.addAll(new CompartmentsReader().read("/org/arachna/netweaver/dctool/commands/"
                + compartmentDirectory));
        }
        catch (final IOException e) {
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
     * command for a given compartment.
     * 
     * @return template to use for creating a 'syncalldcs' in active state
     *         command for a given compartment.
     */
    String getSyncAllDcsForGivenCompartmentInArchiveModeTemplate() {
        return syncAllDcsForGivenCompartmentInArchiveModeTemplate;
    }

    /**
     * @return the syncAllDcsInArchiveModeTemplate
     */
    public String getSyncAllDcsInArchiveModeTemplate() {
        return syncAllDcsInArchiveModeTemplate;
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
     * Return the template for creating exit commands.
     * 
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
     *            the compartment that should be checked for exclusion
     * @return whether the given compartment should be excluded from
     *         synchronization
     */
    public boolean shouldCompartmentBeExcludedFromSynchronization(final Compartment compartment) {
        return excludeSCs.contains(compartment.getName());
    }

    /**
     * Reader for the compartments delivered by SAP to exclude from
     * synchronization.
     * 
     * @author Dirk Weigenand
     */
    static final class CompartmentsReader {
        /**
         * Set of compartments delivered for a certain version of NW.
         * 
         * @param compartmentDirectory
         *            the file containing compartments delivered for a given NW
         *            version
         * @return Set of compartments delivered for a certain version of NW.
         * @throws IOException
         *             when the given directory could not be found.
         */
        Set<String> read(final String compartmentDirectory) throws IOException {
            final Set<String> compartments = new HashSet<String>();

            final InputStream resource = this.getClass().getResourceAsStream(compartmentDirectory);

            if (resource == null) {
                throw new IllegalStateException(String.format("Resource %s not found!", compartmentDirectory));
            }

            final LineNumberReader reader = new LineNumberReader(new InputStreamReader(resource));
            String compartment;
            while ((compartment = reader.readLine()) != null) {
                compartments.add(String.format("sap.com_%s_1", compartment.trim()));
            }

            return compartments;
        }
    }
}
