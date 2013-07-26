/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import org.apache.commons.digester3.Rule;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.xml.RulesModuleProducer;
import org.xml.sax.Attributes;

/**
 * Producer for rules to parse the <code>build.xml</code> of a development
 * component.
 * 
 * @author Dirk Weigenand
 */
public final class BuildXmlRulesModuleProducer implements RulesModuleProducer {
    /**
     * finder to determine whether folders contain unit tests.
     */
    private final TestFolderFinder testFolderFinder;

    /**
     * Create rules producer for <code>build.xml</code> files for building
     * development components. Use default <code>TestFolderFinder</code> to
     * determine folders containing unit tests.
     */
    public BuildXmlRulesModuleProducer() {
        this(new TestFolderFinder());
    }

    /**
     * Create rules producer for <code>build.xml</code> files for building
     * development components. Use the given <code>TestFolderFinder</code> to
     * determine folders containing unit tests.
     * 
     * @param testFolderFinder
     *            finder to determine folders containing unit tests.
     */
    public BuildXmlRulesModuleProducer(final TestFolderFinder testFolderFinder) {
        this.testFolderFinder = testFolderFinder;
    }

    /**
     * Create {@link RulesModule} for parsing a <code>build.xml</code> from a DC
     * build to determine the actual source and output folders used throughout
     * the build.
     * 
     * {@inheritDoc}
     */
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("project/target/javac").addRule(new Rule() {
                    @Override
                    public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
                        final DevelopmentComponent component = getDigester().peek();
                        component.setSourceEncoding(attributes.getValue("encoding"));
                        component.setOutputFolder(attributes.getValue("destdir"));
                    }
                });

                forPattern("project/target/javac/src").addRule(new SourceFolderRule(testFolderFinder));
            }
        };
    }

    /**
     * Rule for handling the 'src' path attributes.
     * 
     * @author Dirk Weigenand
     */
    private static final class SourceFolderRule extends Rule {
        /**
         * finder to determine whether folders contain unit tests.
         */
        private final TestFolderFinder testFolderFinder;

        /**
         * Create rule using the given <code>TestFolderFinder</code>.
         * 
         * @param testFolderFinder
         *            finder to determine whether folders contain unit tests.
         */
        SourceFolderRule(final TestFolderFinder testFolderFinder) {
            this.testFolderFinder = testFolderFinder;
        }

        @Override
        public void begin(final String namespace, final String name, final Attributes attributes) throws Exception {
            final DevelopmentComponent component = getDigester().peek();
            final String sourceFolder = attributes.getValue("path");

            if (testFolderFinder.isTestFolder(component.getSourceEncoding(), sourceFolder)) {
                component.addTestSourceFolder(sourceFolder);
            }
            else {
                component.addSourceFolder(sourceFolder);
            }
        }
    }
}
