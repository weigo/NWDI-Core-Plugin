package org.arachna.netweaver.dc.types;

/**
 * API for calculation of needsRebuild property of development components.
 *
 * @author Dirk Weigenand
 */
public interface NeedsRebuildCalculator {
    /**
     * Determine whether the given development component needs to be rebuilt.
     *
     * @param component the development component to look at.
     * @return whether the development component should be rebuilt ({@code}true{@code}) or not ({@code}false{@code}).
     */
    boolean needsRebuild(DevelopmentComponent component);
}
