/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.netweaver.dc.types.PublicPartType;

/**
 * Factory for example development components for JUnit tests.
 * 
 * @author Dirk Weigenand
 */
public final class ExampleDevelopmentComponentFactory {
    /**
     * example DC.
     */
    public static final String LIB_JETM = "lib/jetm";

    /**
     * Vendor for example development configuration, compartment and development
     * components.
     */
    public static final String EXAMPLE_COM = "example.com";

    /**
     *
     */
    static final String LIB_JDBC = "lib/jdbc";

    /**
     * example DC.
     */
    static final String LIB_JEE_JETM = "lib/jee/jetm";

    /**
     * example DC.
     */
    static final String LIB_JETM_HELPER = "lib/jetm/helper";

    /**
     * example DC.
     */
    static final String LIB_JUNIT = "lib/junit";

    /**
     * assembly public part.
     */
    private static final PublicPart ASSEMBLY = new PublicPart("ASSEMBLY", "assembly PP", "", PublicPartType.ASSEMBLY);

    /**
     * api public part.
     */
    private static final PublicPart API = new PublicPart("API", "api PP", "", PublicPartType.COMPILE);

    /**
     * defLib public part.
     */
    private static final PublicPart DEFLIB = new PublicPart("defLib", "defLib PP", "", PublicPartType.COMPILE);

    /**
     * Should not be instantiated.
     */
    private ExampleDevelopmentComponentFactory() {
    }

    /**
     * Create a {@link DevelopmentComponentFactory} as 'object mother' with an
     * example set of development components and their usage relationships.
     * 
     * @return a <code>DevelopmentComponentFactory</code> with example
     *         development components.
     */
    public static DevelopmentComponentFactory create() {
        final DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();
        dcFactory.create(EXAMPLE_COM, LIB_JETM, new PublicPart[] { ASSEMBLY, API }, new PublicPartReference[] {});

        dcFactory.create(EXAMPLE_COM, LIB_JETM_HELPER, new PublicPart[] { ASSEMBLY, API }, new PublicPartReference[] {
            new PublicPartReference(EXAMPLE_COM, LIB_JETM, API.getPublicPart()),
            new PublicPartReference(EXAMPLE_COM, LIB_JUNIT, API.getPublicPart()) });

        dcFactory.create(EXAMPLE_COM, LIB_JEE_JETM, new PublicPart[] { DEFLIB }, new PublicPartReference[] {
            new PublicPartReference(EXAMPLE_COM, LIB_JETM, ASSEMBLY.getPublicPart()),
            new PublicPartReference(EXAMPLE_COM, LIB_JETM_HELPER, ASSEMBLY.getPublicPart()) });

        dcFactory.create(EXAMPLE_COM, LIB_JUNIT, new PublicPart[] { API }, new PublicPartReference[] {});
        dcFactory.create(EXAMPLE_COM, LIB_JDBC, new PublicPart[] { API }, new PublicPartReference[] {});

        dcFactory.updateUsingDCs();

        return dcFactory;
    }
}
