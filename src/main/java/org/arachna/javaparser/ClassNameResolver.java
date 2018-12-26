/**
 * 
 */
package org.arachna.javaparser;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;

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
     * Mapping for classes in java.lang.
     */
    @SuppressWarnings("serial")
    private static final Map<String, String> JAVA_LANG_CLASSES = new HashMap<String, String>() {
        {
            for (final String className : new String[] { "Appendable", "CharSequence", "Cloneable", "Comparable", "Iterable", "Readable",
                "Runnable", "Thread.UncaughtExceptionHandler", "Boolean", "Byte", "Character", "Character.Subset",
                "Character.UnicodeBlock", "Class", "ClassLoader", "Compiler", "Double", "Enum", "Float", "InheritableThreadLocal",
                "Integer", "Long", "Math", "Number", "Object", "Package", "Process", "ProcessBuilder", "Runtime", "RuntimePermission",
                "SecurityManager", "Short", "StackTraceElement", "StrictMath", "String", "StringBuffer", "StringBuilder", "System",
                "Thread", "ThreadGroup", "ThreadLocal", "Throwable", "Void", "Thread.State", "ArithmeticException",
                "ArrayIndexOutOfBoundsException", "ArrayStoreException", "ClassCastException", "ClassNotFoundException",
                "CloneNotSupportedException", "EnumConstantNotPresentException", "Exception", "IllegalAccessException",
                "IllegalArgumentException", "IllegalMonitorStateException", "IllegalStateException", "IllegalThreadStateException",
                "IndexOutOfBoundsException", "InstantiationException", "InterruptedException", "NegativeArraySizeException",
                "NoSuchFieldException", "NoSuchMethodException", "NullPointerException", "NumberFormatException", "RuntimeException",
                "SecurityException", "StringIndexOutOfBoundsException", "TypeNotPresentException", "UnsupportedOperationException",
                "AbstractMethodError", "AssertionError", "ClassCircularityError", "ClassFormatError", "Error",
                "ExceptionInInitializerError", "IllegalAccessError", "IncompatibleClassChangeError", "InstantiationError", "InternalError",
                "LinkageError", "NoClassDefFoundError", "NoSuchFieldError", "NoSuchMethodError", "OutOfMemoryError", "StackOverflowError",
                "ThreadDeath", "UnknownError", "UnsatisfiedLinkError", "UnsupportedClassVersionError", "VerifyError",
                "VirtualMachineError", "Deprecated", "Override", "SuppressWarnings" }) {
                put(className, "java.lang." + className);
            }
        }
    };

    /**
     * a class name ending with [] indicates an array.
     */
    private static final String BRACKETS = "[]";

    /**
     * mapping from class name to a {@link NameExpr}.
     */
    private final Map<String, Name> classNameMapping = new HashMap<>();

    /**
     * Name of package this ClassNameResolver shall resolve class names for.
     */
    private final String packageName;

    /**
     * Create a <code>ClassNameResolver</code> using the given package name and list of imports.
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
                final Name name = declaration.getName();
                classNameMapping.put(name.getIdentifier(), name);
            }
        }
    }

    /**
     * Determine the full class name (i.e. String &rarr; java.lang.String) of the given {@link Parameter} object.
     * 
     * @param parameter
     *            the Parameter to determine the class name for.
     * @return the class name resolved either from the <code>java.lang</code> package, from the imported classes or the current package.
     */
    public String resolveClassName(final Parameter parameter) {
        return resolveClassName(parameter.getType().toString());
    }

    /**
     * Determine the full class name (i.e. String &rarr; java.lang.String) of the given unqualified class name.
     * 
     * @param className
     *            unqualified class name to determine the fully qualified class name for.
     * @return the class name resolved either from the <code>java.lang</code> package, from the imported classes or the current package.
     */
    public String resolveClassName(final String className) {
        boolean isVarArgs = false;
        String resolvedClassName = className;

        if (className.endsWith(BRACKETS)) {
            resolvedClassName = className.substring(0, className.length() - 2);
            isVarArgs = true;
        }

        if (isFromJavaLangPackage(resolvedClassName)) {
            resolvedClassName = JAVA_LANG_CLASSES.get(resolvedClassName);
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
     * Determine whether the given class name can be loaded from the 'java.lang' package.
     * 
     * @param className
     *            the class name to resolve from the 'java.lang' package.
     * @return <code>true</code> when the given name can be loaded from the 'java.lang' package, <code>false</code> otherwise.
     */
    protected boolean isFromJavaLangPackage(final String className) {
        return JAVA_LANG_CLASSES.containsKey(className);
    }
}
