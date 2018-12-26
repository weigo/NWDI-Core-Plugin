/**
 *
 */
package org.arachna.netweaver.hudson.nwdi;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.arachna.javaparser.ClassNameResolver;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.List;

/**
 * Unit tests for {@link TestAnnotationResolver}.
 *
 * @author Dirk Weigenand
 */
public class TestPropertyResolverTest {
    /**
     * Test method for {@link TestAnnotationResolver#visit(MethodDeclaration, java.lang.Object)}.
     *
     * @throws ParseException when the test class contains errors.
     */
    @Test
    public final void testVisitMethodDeclaration() throws ParseException {
        final CompilationUnit compilationUnit = getCompilationUnit("/org/arachna/netweaver/hudson/nwdi/Junit4Test.java");
        final TestAnnotationResolver resolver = createResolver(compilationUnit);

        for (final TypeDeclaration type : compilationUnit.getTypes()) {
            for (MethodDeclaration method : (List<MethodDeclaration>) type.getMethods()) {
                method.accept(resolver, null);
            }
        }

        assertThat(resolver.junitTestFound(), equalTo(true));
    }

    /**
     * Test method for {@link TestAnnotationResolver#visit(ClassOrInterfaceDeclaration, java.lang.Object)} .
     *
     * @throws ParseException when the test class contains errors.
     */
    @Test
    public final void testVisitClassOrInterfaceDeclarationObject() throws ParseException {
        final CompilationUnit compilationUnit = getCompilationUnit("/org/arachna/netweaver/hudson/nwdi/Junit3Test.java");
        final TestAnnotationResolver resolver = createResolver(compilationUnit);
        compilationUnit.accept(resolver, null);

        assertThat(resolver.junitTestFound(), equalTo(true));
    }

    /**
     * @param compilationUnit
     * @return
     */
    protected TestAnnotationResolver createResolver(final CompilationUnit compilationUnit) {
        PackageDeclaration packageDeclaration = compilationUnit.getPackageDeclaration().get();
        String packageName = packageDeclaration.getNameAsString();
        return new TestAnnotationResolver(new ClassNameResolver(packageName,
                compilationUnit.getImports()));
    }

    /**
     * @param ressourceName
     * @return
     * @throws ParseException
     */
    protected CompilationUnit getCompilationUnit(final String ressourceName) throws ParseException {
        return JavaParser.parse(getClass().getResourceAsStream(ressourceName), Charset.forName("UTF-8"));
    }
}
