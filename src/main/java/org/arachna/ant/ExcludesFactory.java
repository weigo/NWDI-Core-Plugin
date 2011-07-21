package org.arachna.ant;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;

/**
 * Factory for file set exclude expressions based on development component type.
 *
 * @author Dirk Weigenand
 */
public class ExcludesFactory {
    /**
     * Mapping from {@link } to a default set of excludes.
     */
    private Map<DevelopmentComponentType, Collection<String>> excludesMapping =
        new HashMap<DevelopmentComponentType, Collection<String>>();

    /**
     * Create an instance of {@link ExcludesFactory}. Initializes the mapping
     * from development component types to standard ant file set excludes.
     */
    public ExcludesFactory() {
        this.excludesMapping.put(DevelopmentComponentType.WebDynpro, Arrays.asList(new String[] { "**/wdp/*.java", "**/*Interface.java", "**/*InterfaceCfg.java" }));
    }

    /**
     * Returns the excludes that are default for the type of the given
     * development component joined with the given collection of configured
     * excludes.
     *
     * @param component
     *            development component to determine the set of excludes for.
     * @param configuredExcludes
     *            configured excludes
     * @return collection of excludes determined by development component type
     *         and configured excludes.
     */
    public String[] create(DevelopmentComponent component, Collection<String> configuredExcludes) {
        Set<String> excludes = new HashSet<String>();

        Collection<String> defaultExcludes = this.excludesMapping.get(component.getType());

        if (defaultExcludes != null) {
            excludes.addAll(defaultExcludes);
        }

        if (configuredExcludes != null) {
            excludes.addAll(configuredExcludes);
        }

        return excludes.toArray(new String[excludes.size()]);
    }
}
