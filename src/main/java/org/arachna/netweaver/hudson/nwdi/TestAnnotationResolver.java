/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

import org.arachna.javaparser.ClassNameResolver;

/**
 * Visitor to determine whether a given method declaration is a unit test.
 * 
 * @author Dirk Weigenand
 */
public class TestAnnotationResolver extends VoidVisitorAdapter<Object> {
    /**
     * Resolver for class names that are not fully qualified (i.e. from java.lang or the local package).
     */
    private final ClassNameResolver classNameResolver;

    /**
     * indicates whether a visited method declaration was annotated with <code>@Test</code> from the <code>org.junit</code> package.
     */
    private boolean junitTestFound;

    /**
     * Create instance of test annotation resolver using the given class name resolver.
     * 
     * @param classNameResolver
     *            resolver for imported class names.
     */
    public TestAnnotationResolver(final ClassNameResolver classNameResolver) {
        this.classNameResolver = classNameResolver;
    }

    @Override
    public void visit(final MethodDeclaration methodDeclaration, final Object arg) {
        final List<AnnotationExpr> annotations = methodDeclaration.getAnnotations();

        if (annotations != null) {
            for (final AnnotationExpr annotation : annotations) {
                if ("org.junit.Test".equals(classNameResolver.resolveClassName(annotation.getNameAsString()))) {
                    junitTestFound = true;
                    break;
                }
            }
        }
    }

    /**
     * Determine whether the visited class extends <code>org.junit.TestCase</code>.
     * 
     * @param classOrInterfaceDeclaration
     *            the declaration descriptor of the visited class or interface.
     */
    @Override
    public void visit(final ClassOrInterfaceDeclaration classOrInterfaceDeclaration, final Object arg) {
        final List<ClassOrInterfaceType> classOrInterfaces = classOrInterfaceDeclaration.getExtendedTypes();

        if (classOrInterfaces != null) {
            for (final ClassOrInterfaceType classOrInterface : classOrInterfaces) {
                    if ("junit.framework.TestCase".equals(classNameResolver.resolveClassName(classOrInterface.getName().getIdentifier()))) {
                        junitTestFound = true;
                        break;
                    }
            }
        }
    }

    /**
     * @return the isTestFolder
     */
    public boolean junitTestFound() {
        return junitTestFound;
    }
}
