/**
 * 
 */
package org.arachna.javaparser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link ClassNameResolver}.
 * 
 * @author Dirk Weigenand
 */
public class ClassNameResolverTest {
    /**
     * instance under test.
     */
    private ClassNameResolver resolver;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        resolver = new ClassNameResolver("test.package", null);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
        resolver = null;
    }

    /**
     * Test method for
     * {@link org.arachna.javaparser.ClassNameResolver#resolveClassName(java.lang.String)}
     * .
     */
    @Test
    public final void testResolveClassNameString() {
        assertThat(resolver.resolveClassName("String"), equalTo("java.lang.String"));
    }
}
