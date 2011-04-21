/**
 *
 */
package org.arachna.ant;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.Path;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.PublicPartReference;

/**
 * Helper for building ant projects/tasks.
 *
 * @author Dirk Weigenand
 */
public class AntHelper {
    /**
     * template for computing the base path of a development component.
     */
    private static final String BASE_PATH_TEMPLATE = "%s/.dtc/DCs/%s/%s/_comp/";

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * workspace where build takes place.
     */
    private final String pathToWorkspace;

    /**
     * Logger.
     */
    private final PrintStream logger;

    /**
     * Factory for generating file set excludes from development component type.
     */
    private final ExcludesFactory excludeFactory;

    /**
     * Create an instance of the ant helper.
     *
     * @param pathToWorkspace
     *            path to workspace the build is taking place on
     * @param dcFactory
     *            registry for development components
     * @param excludesFactory
     *            factory for generating file set excludes from development
     *            component type.
     * @param logger
     *            logger
     */
    public AntHelper(final String pathToWorkspace, final DevelopmentComponentFactory dcFactory,
        final ExcludesFactory excludeFactory, final PrintStream logger) {
        this.pathToWorkspace = pathToWorkspace;
        this.dcFactory = dcFactory;
        this.excludeFactory = excludeFactory;
        this.logger = logger;
    }

    /**
     * Iterate over the DCs source folders and return those that actually exist
     * in the file system.
     *
     * @param component
     *            development component to get the existing source folders for.
     * @return source folders that exist in the DCs directory structure.
     */
    public Collection<File> getExistingSourceFolders(DevelopmentComponent component) {
        Collection<File> sourceFolders = new ArrayList<File>();
        String componentBase = this.getBaseLocation(component);

        for (String sourceFolder : component.getSourceFolders()) {
            File folder = this.getSourceFolderLocation(componentBase, sourceFolder);

            if (folder.exists()) {
                sourceFolders.add(folder);
            }
            else {
                this.logger.append(String.format("Source folder %s does not exist in %s/%s!", folder.getName(),
                    component.getVendor(), component.getName()));
            }
        }

        return sourceFolders;
    }

    /**
     * Get a file object pointing to the given source folders location in the
     * development components directory structure.
     *
     * @return the file object representing the source folder in the DCs
     *         directory structure.
     */
    private File getSourceFolderLocation(final String componentBase, final String sourceFolder) {
        return new File(String.format("%s/%s", componentBase, sourceFolder).replace('/', File.separatorChar));
    }

    /**
     * Create a {@link Path} representing the class path that is referenced by
     * the given development component via its used DCs and their public parts.
     *
     * @param project
     *            the ant project the class path shall be created with.
     * @param component
     *            development component the ant class path shall be created for.
     */
    public Path createClassPath(Project project, DevelopmentComponent component) {
        Path path = new Path(project);

        for (PublicPartReference ppRef : component.getUsedDevelopmentComponents()) {
            DevelopmentComponent referencedDC = this.dcFactory.get(ppRef.getVendor(), ppRef.getComponentName());

            if (referencedDC != null) {
                FileSet fileSet = new FileSet();
                // FIXME: use factory and consider type of DC and type of
                // compartment
                fileSet.setDir(new File(this.getBaseLocation(referencedDC)));
                fileSet.appendIncludes(new String[] { "**/*.jar", "**/*.par", "**/*.ear", "**/*.wda" });
                path.addFileset(fileSet);
            }
            else {
                this.logger.append(String.format("Referenced DC %s:%s not found in DC factory!\n", ppRef.getVendor(),
                    ppRef.getComponentName()));
            }
        }

        return path;
    }

    /**
     * Returns the absolute path to workspace.
     *
     * @return absolute path to workspace.
     */
    public String getPathToWorkspace() {
        return pathToWorkspace;
    }

    /**
     * Get the location of a development component in workspace.
     *
     * @param component
     * @return
     */
    private String getBaseLocation(DevelopmentComponent component) {
        return String.format(BASE_PATH_TEMPLATE, this.pathToWorkspace, component.getVendor(), component.getName());
    }

    /**
     * Creates a collection of source file sets representing the source folders
     * and the sources to in-/exclude.
     *
     * @param component
     *            development component to create source file sets for.
     * @param excludes
     *            set of standard ant exclude expressions for file sets.
     * @param set
     *            of regular expressions that determine the files that should be
     *            excluded from the source file sets based on their content
     *            matching one of them
     */
    public Collection<FileSet> createSourceFileSets(DevelopmentComponent component, Collection<String> excludes,
        Collection<String> containsRegexpExcludes) {
        Collection<FileSet> sources = new ArrayList<FileSet>();

        for (final File srcFolder : this.getExistingSourceFolders(component)) {
            final FileSet fileSet = new FileSet();
            fileSet.setDir(srcFolder);
            fileSet.setIncludes("**/*.java");
            fileSet.appendExcludes(this.excludeFactory.create(component, excludes));

            sources.add(fileSet);
        }

        if (!containsRegexpExcludes.isEmpty()) {
            FileSelector createContainsRegexpSelectors = this.createContainsRegexpSelectors(containsRegexpExcludes);

            for (FileSet sourceFiles : sources) {
                sourceFiles.add(createContainsRegexpSelectors);
            }
        }

        return sources;
    }

    private FileSelector createContainsRegexpSelectors(Collection<String> containsRegexpExcludes) {
        final OrSelector or = new OrSelector();

        for (final String containsRegexp : containsRegexpExcludes) {
            final ContainsRegexpSelector selector = new ContainsRegexpSelector();
            selector.setExpression(containsRegexp);
            or.add(selector);
        }

        return new NotSelector(or);
    }
}
