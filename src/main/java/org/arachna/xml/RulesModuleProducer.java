/**
 * 
 */
package org.arachna.xml;

import org.apache.commons.digester3.binder.RulesModule;

/**
 * Interface for producers of Digester3 {@link RulesModule} objects.
 * 
 * @author Dirk Weigenand
 */
public interface RulesModuleProducer {
    /**
     * Get an instance of a {@link RulesModule} for this producer.
     * 
     * @return an instance of a {@link RulesModule} for this producer.
     */
    RulesModule getRulesModule();
}
