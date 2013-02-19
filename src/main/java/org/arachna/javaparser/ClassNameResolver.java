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
     * a class name ending with [] indicates an array.
     */
    private static final String BRACKETS = "[]";

    /**
     * Mapping for classes in java.lang.
     */
    @SuppressWarnings("serial")
    private static final Map<String, String> JAVA_LANG = new HashMap<String, String>() {
        {
            put("Appendable", "java.lang.Appendable");
            put("CharSequence", "java.lang.CharSequence");
            put("Cloneable", "java.lang.Cloneable");
            put("Comparable", "java.lang.Comparable");
            put("Iterable", "java.lang.Iterable");
            put("Readable", "java.lang.Readable");
            put("Runnable", "java.lang.Runnable");
            put("Thread.UncaughtExceptionHandler", "java.lang.Thread.UncaughtExceptionHandler");
            put("Boolean", "java.lang.Boolean");
            put("Byte", "java.lang.Byte");
            put("Character", "java.lang.Character");
            put("Character.Subset", "java.lang.Character.Subset");
            put("Character.UnicodeBlock", "java.lang.Character.UnicodeBlock");
            put("Class", "java.lang.Class");
            put("ClassLoader", "java.lang.ClassLoader");
            put("Compiler", "java.lang.Compiler");
            put("Double", "java.lang.Double");
            put("Enum", "java.lang.Enum");
            put("Float", "java.lang.Float");
            put("InheritableThreadLocal", "java.lang.InheritableThreadLocal");
            put("Integer", "java.lang.Integer");
            put("Long", "java.lang.Long");
            put("Math", "java.lang.Math");
            put("Number", "java.lang.Number");
            put("Object", "java.lang.Object");
            put("Package", "java.lang.Package");
            put("Process", "java.lang.Process");
            put("ProcessBuilder", "java.lang.ProcessBuilder");
            put("Runtime", "java.lang.Runtime");
            put("RuntimePermission", "java.lang.RuntimePermission");
            put("SecurityManager", "java.lang.SecurityManager");
            put("Short", "java.lang.Short");
            put("StackTraceElement", "java.lang.StackTraceElement");
            put("StrictMath", "java.lang.StrictMath");
            put("String", "java.lang.String");
            put("StringBuffer", "java.lang.StringBuffer");
            put("StringBuilder", "java.lang.StringBuilder");
            put("System", "java.lang.System");
            put("Thread", "java.lang.Thread");
            put("ThreadGroup", "java.lang.ThreadGroup");
            put("ThreadLocal", "java.lang.ThreadLocal");
            put("Throwable", "java.lang.Throwable");
            put("Void", "java.lang.Void");
            put("Thread.State", "java.lang.Thread.State");
            put("ArithmeticException", "java.lang.ArithmeticException");
            put("ArrayIndexOutOfBoundsException", "java.lang.ArrayIndexOutOfBoundsException");
            put("ArrayStoreException", "java.lang.ArrayStoreException");
            put("ClassCastException", "java.lang.ClassCastException");
            put("ClassNotFoundException", "java.lang.ClassNotFoundException");
            put("CloneNotSupportedException", "java.lang.CloneNotSupportedException");
            put("EnumConstantNotPresentException", "java.lang.EnumConstantNotPresentException");
            put("Exception", "java.lang.Exception");
            put("IllegalAccessException", "java.lang.IllegalAccessException");
            put("IllegalArgumentException", "java.lang.IllegalArgumentException");
            put("IllegalMonitorStateException", "java.lang.IllegalMonitorStateException");
            put("IllegalStateException", "java.lang.IllegalStateException");
            put("IllegalThreadStateException", "java.lang.IllegalThreadStateException");
            put("IndexOutOfBoundsException", "java.lang.IndexOutOfBoundsException");
            put("InstantiationException", "java.lang.InstantiationException");
            put("InterruptedException", "java.lang.InterruptedException");
            put("NegativeArraySizeException", "java.lang.NegativeArraySizeException");
            put("NoSuchFieldException", "java.lang.NoSuchFieldException");
            put("NoSuchMethodException", "java.lang.NoSuchMethodException");
            put("NullPointerException", "java.lang.NullPointerException");
            put("NumberFormatException", "java.lang.NumberFormatException");
            put("RuntimeException", "java.lang.RuntimeException");
            put("SecurityException", "java.lang.SecurityException");
            put("StringIndexOutOfBoundsException", "java.lang.StringIndexOutOfBoundsException");
            put("TypeNotPresentException", "java.lang.TypeNotPresentException");
            put("UnsupportedOperationException", "java.lang.UnsupportedOperationException");
            put("AbstractMethodError", "java.lang.AbstractMethodError");
            put("AssertionError", "java.lang.AssertionError");
            put("ClassCircularityError", "java.lang.ClassCircularityError");
            put("ClassFormatError", "java.lang.ClassFormatError");
            put("Error", "java.lang.Error");
            put("ExceptionInInitializerError", "java.lang.ExceptionInInitializerError");
            put("IllegalAccessError", "java.lang.IllegalAccessError");
            put("IncompatibleClassChangeError", "java.lang.IncompatibleClassChangeError");
            put("InstantiationError", "java.lang.InstantiationError");
            put("InternalError", "java.lang.InternalError");
            put("LinkageError", "java.lang.LinkageError");
            put("NoClassDefFoundError", "java.lang.NoClassDefFoundError");
            put("NoSuchFieldError", "java.lang.NoSuchFieldError");
            put("NoSuchMethodError", "java.lang.NoSuchMethodError");
            put("OutOfMemoryError", "java.lang.OutOfMemoryError");
            put("StackOverflowError", "java.lang.StackOverflowError");
            put("ThreadDeath", "java.lang.ThreadDeath");
            put("UnknownError", "java.lang.UnknownError");
            put("UnsatisfiedLinkError", "java.lang.UnsatisfiedLinkError");
            put("UnsupportedClassVersionError", "java.lang.UnsupportedClassVersionError");
            put("VerifyError", "java.lang.VerifyError");
            put("VirtualMachineError", "java.lang.VirtualMachineError");
            put("Deprecated", "java.lang.Deprecated");
            put("Override", "java.lang.Override");
            put("SuppressWarnings", "java.lang.SuppressWarnings");
        }
    };

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

        if (JAVA_LANG.containsKey(resolvedClassName)) {
            resolvedClassName = JAVA_LANG.get(resolvedClassName);
        }
        else if (classNameMapping.containsKey(resolvedClassName)) {
            resolvedClassName = classNameMapping.get(resolvedClassName).toString();
        }
        else {
            resolvedClassName = packageName + '.' + resolvedClassName;
        }

        return isVarArgs ? resolvedClassName + BRACKETS : resolvedClassName;
    }
}
