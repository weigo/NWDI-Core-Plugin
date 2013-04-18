/**
 * 
 */
package org.arachna.javaparser;

import japa.parser.ast.ImportDeclaration;
import japa.parser.ast.body.Parameter;
import japa.parser.ast.expr.NameExpr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Provider of absolute class names for a given list of imports.
 * 
 * @author Dirk Weigenand
 */
public class ClassNameResolver {
    /**
     * constant for java.lang package.
     */
    private static final String JAVA_LANG = "java.lang.";

    /**
     * a class name ending with [] indicates an array.
     */
    private static final String BRACKETS = "[]";

    /**
     * mapping from class name to a {@link NameExpr}.
     */
    private final Map<String, NameExpr> classNameMapping = new HashMap<String, NameExpr>();

    /**
     * Name of package this ClassNameResolver shall resolve class names for.
     */
    private final String packageName;

    /**
     * Create a <code>ClassNameResolver</code> using the given package name and
     * list of imports.
     * 
     * @param packageName
     *            package name for which to resolve class names.
     * @param imports
     *            list of imports of the class
     */
    public ClassNameResolver(final String packageName, final Collection<ImportDeclaration> imports) {
        this.packageName = packageName;

        if (imports != null) {
            for (final ImportDeclaration declaration : imports) {
                final NameExpr nameExpression = declaration.getName();
                classNameMapping.put(nameExpression.getName(), nameExpression);
            }
        }
    }

    /**
     * Determine the full class name (i.e. String --> java.lang.String) of the
     * given {@link Parameter} object.
     * 
     * @param parameter
     *            the Parameter to determine the class name for.
     * @return the class name resolved either from the <code>java.lang</code>
     *         package, from the imported classes or the current package.
     */
    public String resolveClassName(final Parameter parameter) {
        return resolveClassName(parameter.getType().toString());
    }

    /**
     * Determine the full class name (i.e. String --> java.lang.String) of the
     * given unqualified class name.
     * 
     * @param className
     *            unqualified class name to determine the fully qualified class
     *            name for.
     * @return the class name resolved either from the <code>java.lang</code>
     *         package, from the imported classes or the current package.
     */
    public String resolveClassName(final String className) {
        boolean isVarArgs = false;
        String resolvedClassName = className;

        if (className.endsWith(BRACKETS)) {
            resolvedClassName = className.substring(0, className.length() - 2);
            isVarArgs = true;
        }

        if (isFromJavaLangPackage(resolvedClassName)) {
            resolvedClassName = JAVA_LANG + resolvedClassName;
        }
        else if (classNameMapping.containsKey(resolvedClassName)) {
            resolvedClassName = classNameMapping.get(resolvedClassName).toString();
        }
        else {
            resolvedClassName = packageName + '.' + resolvedClassName;
        }

        return isVarArgs ? resolvedClassName + BRACKETS : resolvedClassName;
    }

    /**
     * Determine whether the given class name can be loaded from the 'java.lang'
     * package.
     * 
     * @param className
     *            the class name to resolve from the 'java.lang' package.
     * @return <code>true</code> when the given name can be loaded from the
     *         'java.lang' package, <code>false</code> otherwise.
     */
    protected boolean isFromJavaLangPackage(final String className) {
        boolean result = false;

        try {
            result = getClass().getClassLoader().loadClass(JAVA_LANG + className) != null;
        }
        catch (final ClassNotFoundException e) {
            // ignore exception when constructed class could not be loaded.
        }

        return result;
    }
}
