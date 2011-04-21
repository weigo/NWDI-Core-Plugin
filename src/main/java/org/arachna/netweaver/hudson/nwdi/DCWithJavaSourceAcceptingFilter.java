/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import org.arachna.netweaver.dc.types.DevelopmentComponent;

/**
 * Check whether a development component can contain java sources based on its
 * {@link DevelopmentComponentType}.
 *
 * @author Dirk Weigenand
 */
public final class DCWithJavaSourceAcceptingFilter implements IDevelopmentComponentFilter {

    /**
     * Accept the given component iff its {@link DevelopmentComponentType}
     * indicates that it can contain Java sources.
     *
     * @return <code>true</code> iff the given DCs
     *         {@link DevelopmentComponentType} indicates that it can contain
     *         Java sources, <code>false</code> otherwise.
     */
    public boolean accept(DevelopmentComponent component) {
        return component != null && component.getType().canContainJavaSources();
    }
}
