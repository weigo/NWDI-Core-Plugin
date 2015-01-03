package org.arachna.netweaver.hudson.nwdi.dcupdater;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.xml.RulesModuleProducer;

/**
 * Update a given development component from its corresponding '.dcdef' file.
 * 
 * @author Dirk Weigenand
 */
class DcDefinitionRulesModuleProducer implements RulesModuleProducer {
    /**
     * Create a rules module for parsing a <code>.dcdef</code> development
     * component configuration file.
     * 
     * @return a rules module for parsing a <code>.dcdef</code> development
     *         component configuration file.
     */
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("development-component/name").setBeanProperty();
                forPattern("development-component/vendor").setBeanProperty();
                forPattern("development-component/caption").setBeanProperty();
                forPattern("development-component/description").setBeanProperty();

                forPattern("development-component/component-type").callMethod("setType").withParamCount(2)
                    .withParamTypes(String.class, String.class);
                forPattern("development-component/component-type/type").callParam().ofIndex(0);
                forPattern("development-component/component-type/sub-type").callParam().ofIndex(1);
                addDependencyRule("development-component/build-plugin", "setBuildPlugin");
                addDependencyRule("development-component/dependencies/dependency", "add");
                forPattern("development-component/folders/package-folder").callMethod("addSourceFolder").withParamCount(1)
                    .withParamTypes(String.class).then().callParam();
                forPattern("development-component/folders/source-folder").callMethod("addResourceFolder").withParamCount(1)
                    .withParamTypes(String.class).then().callParam();
            }

            /**
             * Add a rule for parsing dependencies to other development
             * components or build plugins.
             * 
             * @param prefix
             *            prefix for xml element matching a dependency to
             *            another DC or a build plugin.
             * @param methodName
             *            name of method to use to add/set dependency or build
             *            plugin.
             */
            private void addDependencyRule(final String prefix, final String methodName) {
                forPattern(prefix).createObject().ofType(PublicPartReference.class).usingConstructor(String.class, String.class).then()
                    .setNext(methodName);

                forPattern(prefix + "/dc-ref/vendor").callParam().ofIndex(0);
                forPattern(prefix + "/dc-ref/name").callParam().ofIndex(1);
                forPattern(prefix + "/pp-ref").callMethod("setName").withParamCount(1).withParamTypes(String.class).then().callParam();
                forPattern(prefix + "/at-build-time").callMethod("setAtBuildTime");
                forPattern(prefix + "/at-run-time").callMethod("setAtRunTime");
            }
        };
    }
}
