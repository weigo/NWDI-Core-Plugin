/**
 *
 */
package org.arachna.ant;

import hudson.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.netweaver.dc.types.PublicPartType;

/**
 * Helper for building ant projects/tasks.
 * 
 * @author Dirk Weigenand
 */
public class AntHelper {
    /**
     * template for computing the absolute base path of a development component.
     */
    private static final String BASE_PATH_TEMPLATE = "%s/.dtc/DCs/%s/%s/_comp";

    /**
     * An all folder names accepting filter.
     */
    private static final SourceDirectoryFilter ALL_SOURCE_FOLDERS_ACCEPTING_FILTER =
        new AllSourceFoldersAcceptingFilter();

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * workspace where build takes place.
     */
    private final transient String pathToWorkspace;

    /**
     * Create an instance of the ant helper.
     * 
     * @param pathToWorkspace
     *            path to workspace the build is taking place on
     * @param dcFactory
     *            registry for development components
     */
    public AntHelper(final String pathToWorkspace, final DevelopmentComponentFactory dcFactory) {
        this.pathToWorkspace = pathToWorkspace;
        this.dcFactory = dcFactory;
    }

    /**
     * Iterate over the DCs source folders and return those that actually exist
     * in the file system.
     * 
     * @param component
     *            development component to get the existing source folders for.
     * @param filter
     *            determines which source folders should be included in the
     *            resulting collection of source folders
     * @return source folders that exist in the DCs directory structure.
     */
    private Collection<File> getExistingSourceFolders(final DevelopmentComponent component,
        final SourceDirectoryFilter filter) {
        final Collection<File> sourceFolders = new ArrayList<File>();

        for (final String sourceFolder : component.getSourceFolders()) {
            final File folder = new File(sourceFolder);

            if (folder.exists() && filter.accept(folder.getAbsolutePath())) {
                sourceFolders.add(folder);
            }
        }

        return sourceFolders;
    }

    /**
     * Create a set of paths using the public part references of the given
     * development component.
     * 
     * @param component
     *            development component the ant class path shall be created for.
     * 
     * @return set of paths built from the public part references of the given
     *         development component
     */
    public Set<String> createClassPath(final DevelopmentComponent component) {
        final Set<String> paths = new HashSet<String>();

        for (final PublicPartReference ppRef : component.getUsedDevelopmentComponents()) {
            final DevelopmentComponent referencedDC = dcFactory.get(ppRef.getVendor(), ppRef.getComponentName());

            if (referencedDC != null) {
                final File baseDir = new File(this.getBaseLocation(referencedDC, ppRef.getName()));

                if (!baseDir.exists()) {
                    System.err.println(String.format("createClassPath: cannot find %s for %s~%s:%s!",
                        baseDir.getAbsolutePath(), referencedDC.getVendor(), referencedDC.getName(), ppRef.getName()));
                    continue;
                }

                paths.add(baseDir.getAbsolutePath());
            }
            else {
                System.err.println(String.format("createClassPath: cannot find DC %s:%s!", ppRef.getVendor(),
                    ppRef.getComponentName()));
            }
        }

        return paths;
    }

    /**
     * Calculate the absolute path to the artifacts of the public part in the
     * given development component.
     * 
     * @param component
     *            development component
     * @param name
     *            name of public part
     * @return absolute path to artifacts of the given public part in the given
     *         development component.
     */
    public String getBaseLocation(final DevelopmentComponent component, final String name) {
        String ppName = name;

        if (Util.fixEmpty(ppName) == null) {
            for (final PublicPart pp : component.getPublicParts()) {
                if (PublicPartType.COMPILE.equals(pp.getType())) {
                    ppName = pp.getPublicPart();
                    break;
                }
            }
        }

        return getPublicPartLocation(this.getBaseLocation(component), ppName);
    }

    /**
     * Return the location of the public part relative to the given base
     * location.
     * 
     * The location is that of the given public part under
     * <code>gen/default</code> or the <code>gen/default</code> folder.
     * 
     * @param baseLocation
     *            location of development component
     * @param name
     *            name of public part.
     * @return the path to the artifacts of the given public part in the given
     *         location of development component.
     */
    String getPublicPartLocation(final String baseLocation, final String name) {
        return Util.fixEmpty(name) != null ? String.format("%s/gen/default/public/%s/lib/java", baseLocation, name)
            : String.format("%s/gen/default", baseLocation);
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
     *            development component the location is to be calculated for.
     * @return the location of the given component in the workspace.
     */
    public String getBaseLocation(final DevelopmentComponent component) {
        return String.format(BASE_PATH_TEMPLATE, pathToWorkspace, component.getVendor(), component.getName());
    }

    /**
     * Get the location of the given development component relative to the
     * current workspace.
     * 
     * @param component
     *            development component to get the location relative to the
     *            current workspace.
     * @return location of given DC relative to the current workspace.
     */
    // public String getBaseLocationRelativeToWorkspace(final
    // DevelopmentComponent component) {
    // return String.format(RELATIVE_BASE_PATH_TEMPLATE, component.getVendor(),
    // component.getName());
    // }

    /**
     * Get the location of a development components public part in workspace.
     * 
     * @param ppRef
     *            reference to public part
     * @return the location of the files comprising the given public part
     *         reference
     */
    public String getLocation(final PublicPartReference ppRef) {
        return getPublicPartLocation(this.getBaseLocation(ppRef.getVendor(), ppRef.getComponentName()), ppRef.getName());
    }

    /**
     * Calculate the base path using the given vendor and component name.
     * 
     * @param vendor
     *            the vendor of the given component
     * @param name
     *            the name of the component
     * @return base path using the given vendor and component name.
     */
    private String getBaseLocation(final String vendor, final String name) {
        return String.format(BASE_PATH_TEMPLATE, pathToWorkspace, vendor, name);
    }

    /**
     * Creates a collection of source folders for the given development
     * component.
     * 
     * @param component
     *            development component to create source file sets for.
     * @return a collection of source folders of the given development component
     */
    public Collection<String> createSourceFileSets(final DevelopmentComponent component) {
        return createSourceFileSets(component, ALL_SOURCE_FOLDERS_ACCEPTING_FILTER);
    }

    /**
     * Creates a collection of source folders for the given development
     * component filtered using the given filter.
     * 
     * @param component
     *            development component to create source file sets for.
     * @param filter
     *            a filter that determines which of the source folders should be
     *            added to the result list
     * @return a collection of source folders of the given development component
     *         filtered using the given filter for source directories
     */
    public Collection<String> createSourceFileSets(final DevelopmentComponent component,
        final SourceDirectoryFilter filter) {
        final Collection<String> sources = new HashSet<String>();

        for (final File srcFolder : getExistingSourceFolders(component, filter)) {
            sources.add(srcFolder.getAbsolutePath());
        }

        return sources;
    }
}
