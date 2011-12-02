package org.arachna.netweaver.dctool;

/**
 * Container for parameters relevant to DC tool execution.
 * 
 * @author Dirk Weigenand
 */
public final class DCToolDescriptor {
    /**
     * default options that should be passed to JDK executing the dctool.
     */
    private static final String DEFAULT_JDK_OPTS = "-Xmx256m -Xss20m";

    /**
     * folder name that contains the DTR configuration files.
     */
    public static final String DTR_FOLDER = ".dtr";

    /**
     * folder name that contains the development configuration.
     */
    public static final String DTC_FOLDER = ".dtc";

    /**
     * UME user for authentication against the NWDI.
     */
    private final String user;

    /**
     * password to use authenticating the user.
     */
    private final String password;

    /**
     * path to NWDI tool library folder.
     */
    private final String nwdiToolLibrary;

    /**
     * Options that should be passed to the JDK executing the DC tool.
     */
    private String jdkOpts = DEFAULT_JDK_OPTS;

    /**
     * configured JDKs.
     */
    private final JdkHomePaths paths;

    /**
     * Create an instance of a <code>DCToolDescriptor</code>.
     * 
     * @param user
     *            UME user for authentication against the NWDI.
     * @param password
     *            password to use authenticating the user.
     * @param nwdiToolLibrary
     *            path to NWDI tool library folder.
     * @param paths
     *            configured JDKs.
     * @param jdkOpts
     *            Options that should be passed to the JDK executing the DC
     *            tool.
     */
    public DCToolDescriptor(final String user, final String password, final String nwdiToolLibrary,
        final JdkHomePaths paths, final String jdkOpts) {
        super();
        this.user = user;
        this.password = password;
        this.nwdiToolLibrary = nwdiToolLibrary;
        this.paths = paths;
        this.jdkOpts = jdkOpts;
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @return the nwdiToolLibrary
     */
    public String getNwdiToolLibrary() {
        return nwdiToolLibrary;
    }

    /**
     * @return the paths
     */
    public JdkHomePaths getPaths() {
        return paths;
    }

    /**
     * Returns the options that should be passed to the JDK executing the DC
     * tool.
     * 
     * @return options that should be passed to the JDK executing the DC tool.
     */
    public String getJdkOpts() {
        return this.jdkOpts == null || this.jdkOpts.isEmpty() ? DEFAULT_JDK_OPTS : this.jdkOpts;
    }
}
