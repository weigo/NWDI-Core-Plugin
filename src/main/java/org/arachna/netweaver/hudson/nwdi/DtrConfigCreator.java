/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import hudson.FilePath;
import hudson.Util;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Stack;

import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Create or update the dtr configuration files.
 * 
 * @author Dirk Weigenand
 */
final class DtrConfigCreator {
    /**
     * constant for NWDI workspace directory.
     */
    static final String DOT_DTC = ".dtc";

    /**
     * constant for DTR configuration directory.
     */
    static final String DOT_DTR = ".dtr";

    /**
     * constant for 'clients.xml' configuration file.
     */
    static final String CLIENTS_XML = "clients.xml";

    /**
     * default encoding to use.
     */
    static final String DEFAULT_ENCODING = "UTF-8";

    /**
     * constant for 'servers.xml' configuration file.
     */
    static final String SERVERS_XML = "servers.xml";

    /**
     * {@link DevelopmentConfiguration} to use for creating the dtr config
     * files.
     */
    private final DevelopmentConfiguration config;

    /**
     * workspace to create dtr configuration in.
     */
    private final FilePath workspace;

    /**
     * content of '.confdef' for the given development configuration.
     */
    private final String confDef;

    /**
     * Create an instance of {@link DtrConfigCreator}.
     * 
     * @param workspace
     *            the workspace where the configuration folders and files should
     *            be created/updated.
     * @param config
     *            the {@link DevelopmentConfiguration} that shall be used to
     *            create/update the configuration files.
     * @param confDef
     *            the content of the <code>.confdef</code> of the given
     *            development configuration.
     */
    DtrConfigCreator(final FilePath workspace, final DevelopmentConfiguration config, final String confDef) {
        this.workspace = workspace;
        this.config = config;
        this.confDef = confDef;
    }

    /**
     * Creates/Updates the DTR and development configuration configuration
     * files.
     * 
     * @return the {@link FilePath} created for the DTR configuration directory.
     * @throws IOException
     *             when an error occurred creating the directories and
     *             configuration files.
     * @throws InterruptedException
     *             when the user canceled the operation.
     */
    FilePath execute() throws IOException, InterruptedException {
        final FilePath dtrDirectory = createFolder(DOT_DTR);
        final FilePath dtcDirectory = createFolder(DOT_DTC);

        createOrUpdateServersXml(dtrDirectory);
        createOrUpdateClientsXml(dtrDirectory, dtcDirectory);
        createOrUpdateDotConfDef(dtcDirectory);

        return dtrDirectory;
    }

    /**
     * Creates/Updates the <code>.confdef</code> file.
     * 
     * @param dtcDirectory
     *            directory where <code>.confdef</code> file should be created.
     * @throws IOException
     *             when an error occurred creating the configuration file.
     * @throws InterruptedException
     *             when the user canceled the operation.
     */
    private void createOrUpdateDotConfDef(final FilePath dtcDirectory) throws IOException, InterruptedException {
        final FilePath confDef = dtcDirectory.child(".confdef");
        confDef.write(this.confDef, DEFAULT_ENCODING);
    }

    /**
     * Creates the given folder in the workspace.
     * 
     * @param folderName
     *            the folder to be created.
     * @return the {@link FilePath} created.
     * @throws IOException
     *             when an error occurred creating the given folder in the
     *             workspace.
     * @throws InterruptedException
     *             when the user canceled the operation.
     */
    private FilePath createFolder(final String folderName) throws IOException, InterruptedException {
        final FilePath folder = this.workspace.child(folderName);

        if (!folder.exists()) {
            folder.mkdirs();
        }

        return folder;
    }

    /**
     * Creates/Updates the 'clients.xml' in the given DTR folder.
     * 
     * @param dtrDirectory
     *            folder for DTR configuration files.
     * @param dtcDirectory
     *            folder for '.confdef' development configuration file.
     * @throws IOException
     *             when an error occurred creating the configuration file in the
     *             given folder.
     * @throws InterruptedException
     *             when the user canceled the operation.
     */
    private void createOrUpdateClientsXml(final FilePath dtrDirectory, final FilePath dtcDirectory) throws IOException,
        InterruptedException {
        final String path = makeAbsolute(dtcDirectory);

        final String content =
            String.format(this.getTemplate(CLIENTS_XML), this.config.getName(), path.toString(), this.config.getName());
        dtrDirectory.child(CLIENTS_XML).write(content, DEFAULT_ENCODING);
    }

    /**
     * Creates/Updates the 'servers.xml' in the given DTR configuration folder.
     * 
     * @param dtrDirectory
     *            folder for DTR configuration files.
     * @throws IOException
     *             when an error occurred creating the configuration file in the
     *             given folder.
     * @throws InterruptedException
     *             when the user canceled the operation.
     */
    private void createOrUpdateServersXml(final FilePath dtrDirectory) throws IOException, InterruptedException {
        dtrDirectory.child(SERVERS_XML).write(
            String.format(this.getTemplate(SERVERS_XML), this.config.getBuildServer()), DEFAULT_ENCODING);
    }

    /**
     * Returns the absolute path of the given <code>path</code>.
     * 
     * @param path
     *            the {@link FilePath} to compute the absolute path for.
     * @return absolute path for the given file path.
     */
    protected String makeAbsolute(final FilePath path) {
        final Stack<String> paths = new Stack<String>();
        FilePath parent = path;

        while (parent != null) {
            paths.push(parent.getName());
            parent = parent.getParent();
        }

        final StringBuilder absolutePath = new StringBuilder();

        while (!paths.isEmpty()) {
            absolutePath.append(paths.pop()).append(File.separatorChar);
        }

        return absolutePath.toString();
    }

    /**
     * Get the content of the given template.
     * 
     * @param templateName
     *            name of the template to load from class path.
     * 
     * @return content of the given template.
     * @throws IOException
     *             when reading the template fails.
     */
    private String getTemplate(final String templateName) throws IOException {
        final StringWriter content = new StringWriter();
        Util.copyStreamAndClose(new InputStreamReader(this.getClass().getResourceAsStream(templateName)), content);

        return content.toString();
    }
}
