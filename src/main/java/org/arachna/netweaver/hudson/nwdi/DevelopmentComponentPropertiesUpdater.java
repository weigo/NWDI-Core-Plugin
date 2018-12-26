/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.DevelopmentConfigurationVisitor;
import org.arachna.xml.DigesterHelper;

/**
 * Update development components source folders, source encodings and output folder.
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
    private final DigesterHelper<DevelopmentComponent> digesterHelper =
        new DigesterHelper<DevelopmentComponent>(new BuildXmlRulesModuleProducer());

    /**
     * Create updater for development component properties with the given {@link AntHelper} instance.
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

            if (buildXml.exists()) {
                // source folders have been read from .dcdef and are relative to
                // DC base location. Replace with absolute paths from
                // build.xml.
                component.setSourceFolders(null);

                try {
                    digesterHelper.update(new InputStreamReader(new FileInputStream(buildXml), Charset.forName("UTF-8")), component);
                }
                catch (final IllegalStateException ise) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, String.format(
                        "IllegalStateException occured while updating '%s' from '%s'.", component, buildXml.getAbsolutePath()), ise);
                }
                catch (final NullPointerException npe) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE,
                        String.format("NullPointerException occured while updating '%s' from '%s'.", component.getNormalizedName("~"),
                            buildXml.getAbsolutePath()),
                        npe);
                }

            }
            else {
                final String base = antHelper.getBaseLocation(component);

                component.setSourceFolders(makeFoldersAbsolute(base, component.getSourceFolders()));
                component.setTestSourceFolders(makeFoldersAbsolute(base, component.getTestSourceFolders()));
            }
        }
        catch (final FileNotFoundException e) {
            // ignore: component was not built yet.
        }
    }

    /**
     * Prepend component base path to set of folders.
     * 
     * @param base
     *            component base path.
     * @param folders
     *            set of folders relative to base path.
     * @return set of absolute paths create from the given set of paths.
     */
    private Set<String> makeFoldersAbsolute(final String base, final Set<String> folders) {
        final Set<String> sourceFolders = new HashSet<String>();

        for (final String folder : folders) {
            sourceFolders.add(new File(base, folder).getAbsolutePath());
        }

        return sourceFolders;
    }
}
