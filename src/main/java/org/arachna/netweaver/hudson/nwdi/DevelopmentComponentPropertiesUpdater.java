/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.DevelopmentConfigurationVisitor;
import org.arachna.xml.DigesterHelper;

/**
 * Update development components source folders, source encodings and output
 * folder.
 * 
 * @author Dirk Weigenand
 */
final class DevelopmentComponentPropertiesUpdater implements DevelopmentConfigurationVisitor {
    /**
     * build helper.
     */
    private final AntHelper antHelper;

    /**
     * parser for <code>build.xml</code> files.
     */
    private final DigesterHelper<DevelopmentComponent> digesterHelper = new DigesterHelper<DevelopmentComponent>(
        new BuildXmlRulesModuleProducer());

    /**
     * Create updater for development component properties with the given
     * {@link AntHelper} instance.
     * 
     * @param antHelper
     *            build helper
     */
    DevelopmentComponentPropertiesUpdater(final AntHelper antHelper) {
        this.antHelper = antHelper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentConfiguration configuration) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final Compartment compartment) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentComponent component) {
        if (CompartmentState.Archive.equals(component.getCompartment().getState())) {
            return;
        }

        try {
            final File buildXml = new File(antHelper.getBaseLocation(component), "gen/default/logs/build.xml");
            digesterHelper.update(new InputStreamReader(new FileInputStream(buildXml), Charset.forName("UTF-8")),
                component);
        }
        catch (final FileNotFoundException e) {
            // ignore: component was not built yet.
        }
    }
}
