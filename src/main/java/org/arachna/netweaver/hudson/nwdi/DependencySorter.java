/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import java.util.Collection;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Target;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;

/**
 * Sort development components in needing a rebuild in build order.
 * 
 * @author g526521
 */
public final class DependencySorter {
    /**
     * separator in target names.
     */
    private static final String COLON = ":";

    /**
     * name of root target.
     */
    private static final String ROOT_TARGET = "root";

    /**
     * Collection of development components the build sequence is to be
     * determined.
     */
    private final Collection<DevelopmentComponent> components;

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Create an instance of a <code>DependencySorter</code> using the given
     * list of {@link DevelopmentComponent} objects.
     * 
     * @param dcFactory
     *            registry for development components
     * @param components
     *            Collection of development components the build sequence is to
     *            be determined.
     * 
     */
    public DependencySorter(final DevelopmentComponentFactory dcFactory,
        final Collection<DevelopmentComponent> components) {
        this.dcFactory = dcFactory;
        this.components = components;
    }

    /**
     * Determine the sequence the given development components should be built
     * in.
     * 
     * @return a collection of development components in the sequence they
     *         should be built in.
     */
    @SuppressWarnings("unchecked")
    public List<DevelopmentComponent> determineBuildSequence() {
        final List<DevelopmentComponent> sequence = new LinkedList<DevelopmentComponent>();
        final Hashtable<String, Target> targets = new Hashtable<String, Target>();
        final Project project = this.createProject(targets);

        for (final Target target : (Vector<Target>) project.topoSort(ROOT_TARGET, targets)) {
            if (target.getName().contains(COLON)) {
                final String[] parts = target.getName().split(COLON);
                sequence.add(0, this.dcFactory.get(parts[0], parts[1]));
            }
        }

        return sequence;
    }

    /**
     * Create an Ant project with respective targets and dependencies from the
     * collection of development components.
     * 
     * @param targets
     *            hash table used when sorting the targets.
     * @return a sample project used to sort the targets.
     */
    private Project createProject(final Hashtable<String, Target> targets) {
        final Project project = new Project();
        final Target root = new Target();
        root.setName(ROOT_TARGET);
        project.addTarget(root);

        targets.put(root.getName(), root);

        for (final DevelopmentComponent component : this.components) {
            project.addOrReplaceTarget(createTargets(targets, project, component, root));
        }

        return project;
    }

    /**
     * Create a target for the given development component.
     * 
     * The created target will be added to the given parent, project and targets
     * hashtable. For all development components that use the given development
     * component the method will be called recursively.
     * 
     * @param targets
     *            table for collecting targets.
     * @param project
     *            ant project the targets should be added to.
     * @param component
     *            development component to create target for.
     * @param parent
     *            the parent target dependencies should be added to
     * @return the created target
     */
    private Target createTargets(final Hashtable<String, Target> targets, final Project project,
        final DevelopmentComponent component, final Target parent) {
        final Target target = new Target();
        target.setName(this.createTargetName(component.getVendor(), component.getName()));
        targets.put(target.getName(), target);
        parent.addDependency(target.getName());

        for (final DevelopmentComponent usingDC : component.getUsingDevelopmentComponents()) {
            project.addOrReplaceTarget(this.createTargets(targets, project, usingDC, target));
        }

        return target;
    }

    /**
     * Create a target name as concatenation of the given vendor, ':' and name.
     * 
     * @param vendor
     *            Vendor of the component the target name shall be created for.
     * @param name
     *            Name of the component the target name shall be created for.
     * @return target name as concatenation of the given vendor, ':' and name.
     */
    private String createTargetName(final String vendor, final String name) {
        return vendor + COLON + name;
    }
}
