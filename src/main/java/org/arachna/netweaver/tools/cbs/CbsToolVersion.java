/**
 * 
 */
package org.arachna.netweaver.tools.cbs;

import org.apache.commons.lang.StringUtils;

/**
 * Enumeration of CBS tool versions.
 * 
 * @author Dirk Weigenand
 * 
 */
public enum CbsToolVersion {
    /**
     * CE version of CBS tool.
     */
    CE("com.sap.tc.di.cmdline.ApplicationLauncher", "tc~di~cl~application_launcher_api.jar"),

    /**
     * pre CE version of CBS tool.
     */
    PRE_CE("com.sap.tc.cbstool.startup.CBSToolMain", "cbstoolstartup.jar");

    /**
     * application to start the CBS tool.
     */
    private final String application;

    /**
     * archive containing CBS tool.
     */
    private final String startupArchive;

    /**
     * Create a CbsToolVersion with the given application and startup archive.
     * 
     * @param application
     *            application used to start CBS tool.
     * @param startupArchive
     *            archive name containing the application.
     */
    private CbsToolVersion(final String application, final String startupArchive) {
        this.application = application;
        this.startupArchive = startupArchive;
    }

    /**
     * Determines the appropriate CbsToolVersion from the given string.
     * 
     * Throws an {@link IllegalStateException} when the version cannot be
     * determined.
     * 
     * @param cbsToolCommand
     *            content of a 'cbstool.sh' or 'cbstool.cmd'.
     * @return the CBS tool version appropriate for the given input.
     */
    public static CbsToolVersion fromString(final String cbsToolCommand) {
        if (StringUtils.isNotBlank(cbsToolCommand)) {
            for (final CbsToolVersion version : values()) {
                if (cbsToolCommand.contains(version.application) && cbsToolCommand.contains(version.startupArchive)) {
                    return version;
                }
            }
        }

        throw new IllegalStateException("Unknown CBS tool version!");
    }
}
