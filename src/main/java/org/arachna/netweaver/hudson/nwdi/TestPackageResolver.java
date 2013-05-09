/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import japa.parser.ast.body.MethodDeclaration;
import japa.parser.ast.expr.AnnotationExpr;
import japa.parser.ast.expr.NameExpr;
import japa.parser.ast.visitor.VoidVisitorAdapter;

import java.util.List;

import org.arachna.javaparser.ClassNameResolver;

/**
 * Visitor to determine
 * 
 * @author Dirk Weigenand
 */
public class TestPackageResolver extends VoidVisitorAdapter {
    private final ClassNameResolver classNameResolver;
    private boolean isTestFolder;

    public TestPackageResolver(final ClassNameResolver classNameResolver) {
        this.classNameResolver = classNameResolver;
    }

    @Override
    public void visit(final MethodDeclaration methodDeclaration, final Object arg) {
        final List<AnnotationExpr> annotations = methodDeclaration.getAnnotations();

        if (annotations != null) {
            for (final AnnotationExpr annotation : annotations) {
                final NameExpr nameExpr = annotation.getName();
                if ("org.junit.Test".equals(classNameResolver.resolveClassName(nameExpr.getName()))) {
                    isTestFolder = true;
                    break;
                }
            }
        }
    }

    /**
     * @return the isTestFolder
     */
    public boolean isTestFolder() {
        return isTestFolder;
    }
}
