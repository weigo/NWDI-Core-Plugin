/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import japa.parser.ast.body.ClassOrInterfaceDeclaration;
import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.type.ClassOrInterfaceType;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

import org.arachna.javaparser.ClassNameResolver;

/**
 * Visitor to determine whether a given method declaration is a unit test.
 * 
 * @author Dirk Weigenand
 */
public class TestPropertyResolver extends VoidVisitorAdapter {
    /**
     * Resolver for class names that are not fully qualified (i.e. from java.lang or the local package).
     */
    private final ClassNameResolver classNameResolver;

    /**
     * indicates whether a visited method declaration was annotated with <code>@Test</code> from the <code>org.junit</code> package.
     */
    private boolean junitTestFound;

    public TestPropertyResolver(final ClassNameResolver classNameResolver) {
        this.classNameResolver = classNameResolver;
    }

    @Override
    public void visit(final MethodDeclaration methodDeclaration, final Object arg) {
        final List<AnnotationExpr> annotations = methodDeclaration.getAnnotations();

        if (annotations != null) {
            for (final AnnotationExpr annotation : annotations) {
                final NameExpr nameExpr = annotation.getName();
                if ("org.junit.Test".equals(classNameResolver.resolveClassName(nameExpr.getName()))) {
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
        final List<ClassOrInterfaceType> classOrInterfaces = classOrInterfaceDeclaration.getExtends();

        if (classOrInterfaces != null) {
            for (final ClassOrInterfaceType classOrInterface : classOrInterfaces) {
                if ("junit.framework.TestCase".equals(classNameResolver.resolveClassName(classOrInterface.getName()))) {
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
