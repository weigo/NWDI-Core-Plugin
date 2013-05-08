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
                forPattern("project/target/javac/src").addRule(new Rule() {
                    @Override
                    public void begin(final String namespace, final String name, final Attributes attributes)
                        throws Exception {
                        final DevelopmentComponent component = getDigester().peek();
                        component.addSourceFolder(attributes.getValue("path"));
                    }
                });
                forPattern("project/target/javac").addRule(new Rule() {
                    @Override
                    public void begin(final String namespace, final String name, final Attributes attributes)
                        throws Exception {
                        final DevelopmentComponent component = getDigester().peek();
                        component.setSourceEncoding(attributes.getValue("encoding"));
                        component.setOutputFolder(attributes.getValue("destdir"));
                    }
                });
            }
        };
    }
}
