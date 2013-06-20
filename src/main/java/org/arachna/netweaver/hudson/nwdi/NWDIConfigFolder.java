/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

/**
 * @author Dirk Weigenand
 */
public enum NWDIConfigFolder {
    /**
     * folder name that contains the development configuration.
     */
    DTC(".dtc"),

    /**
     * folder name that contains the DTR configuration files.
     */
    DTR(".dtr");

    /**
     * the actual name of the workspace sub folder.
     */
    private final String folderName;

    /**
     * Create new instance of config folder with the given name.
     * 
     * @param folderName
     *            name of sub folder in workspace.
     */
    private NWDIConfigFolder(final String folderName) {
        this.folderName = folderName;
    }

    /**
     * Return the actual sub folder name.
     * 
     * @return sub folder name.
     */
    public String getName() {
        return folderName;
    }
}
