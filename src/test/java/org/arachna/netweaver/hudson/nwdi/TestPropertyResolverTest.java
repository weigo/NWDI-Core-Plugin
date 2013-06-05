/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.TypeDeclaration;

import org.arachna.javaparser.ClassNameResolver;
import org.junit.Test;

/**
 * Unit tests for {@link TestPropertyResolver}.
 * 
 * @author Dirk Weigenand
 */
public class TestPropertyResolverTest {
    /**
     * Test method for {@link TestPropertyResolver#visit(japa.parser.ast.body.MethodDeclaration, java.lang.Object)}.
     * 
     * @throws ParseException
     *             when the test class contains errors.
     */
    @Test
    public final void testVisitMethodDeclaration() throws ParseException {
        final CompilationUnit compilationUnit = getCompilationUnit("/org/arachna/netweaver/hudson/nwdi/Junit4Test.java");
        final TestPropertyResolver resolver = createResolver(compilationUnit);

        for (final TypeDeclaration type : compilationUnit.getTypes()) {
            for (final BodyDeclaration body : type.getMembers()) {
                body.accept(resolver, null);
            }
        }

        assertThat(resolver.junitTestFound(), equalTo(true));
    }

    /**
     * Test method for {@link TestPropertyResolver#visit(japa.parser.ast.body.ClassOrInterfaceDeclaration, java.lang.Object)} .
     * 
     * @throws ParseException
     *             when the test class contains errors.
     */
    @Test
    public final void testVisitClassOrInterfaceDeclarationObject() throws ParseException {
        final CompilationUnit compilationUnit = getCompilationUnit("/org/arachna/netweaver/hudson/nwdi/Junit3Test.java");
        final TestPropertyResolver resolver = createResolver(compilationUnit);
        compilationUnit.accept(resolver, null);

        assertThat(resolver.junitTestFound(), equalTo(true));
    }

    /**
     * @param compilationUnit
     * @return
     */
    protected TestPropertyResolver createResolver(final CompilationUnit compilationUnit) {
        return new TestPropertyResolver(new ClassNameResolver(compilationUnit.getPackage().getName().getName(),
            compilationUnit.getImports()));
    }

    /**
     * @param ressourceName
     * @return
     * @throws ParseException
     */
    protected CompilationUnit getCompilationUnit(final String ressourceName) throws ParseException {
        return JavaParser.parse(getClass().getResourceAsStream(ressourceName), "UTF-8");
    }
}
