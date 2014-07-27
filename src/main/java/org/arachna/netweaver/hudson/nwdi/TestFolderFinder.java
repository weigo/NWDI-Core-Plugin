/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;
import japa.parser.ast.PackageDeclaration;
import japa.parser.ast.body.BodyDeclaration;
import japa.parser.ast.body.TypeDeclaration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.javaparser.ClassNameResolver;
import org.arachna.util.io.FileFinder;

/**
 * Finder to determine whether folders contain unit tests.
 * 
 * @author Dirk Weigenand
 */
class TestFolderFinder {
    /**
     * Logger.
     */
    private final Logger logger = Logger.getLogger(this.getClass().getName());

    /**
     * Determine whether the given source folder contains unit tests.
     * 
     * @param encoding
     *            encoding to use for reading of source files.
     * @param sourceFolder
     *            source folder from <code>build.xml</code>.
     * @return <code>true</code> when there are sources in the given folder
     *         containing unit tests, <code>false</code> else.
     */
    boolean isTestFolder(final String encoding, final String sourceFolder) {
        for (final InputStream source : getJavaSources(encoding, sourceFolder)) {
            try {
                final CompilationUnit compilationUnit = JavaParser.parse(source, encoding);
                final PackageDeclaration packageDescriptor = compilationUnit.getPackage();

                if (packageDescriptor != null && compilationUnitContainsJUnitTest(compilationUnit, packageDescriptor)) {
                    return true;
                }
            }
            catch (final ParseException e) {
                logger.log(Level.WARNING, e.getLocalizedMessage(), e);
            }
        }

        return false;
    }

    protected Collection<InputStream> getJavaSources(final String encoding, final String sourceFolder) {
        final FileFinder finder = new FileFinder(new File(sourceFolder), ".*\\.java");
        final Collection<InputStream> sources = new ArrayList<InputStream>();

        for (final File file : finder.find()) {
            try {
                sources.add(new FileInputStream(file));
            }
            catch (final FileNotFoundException e) {
                logger.log(Level.WARNING, e.getLocalizedMessage(), e);
            }
        }

        return sources;
    }

    /**
     * Determine whether the given compilation unit contains JUnit tests.
     * 
     * @param compilationUnit
     *            the compilation unit to test for JUnit tests.
     * @param packageDescriptor
     *            descriptor of package of compilation unit.
     * @return <code>true</code> when a JUnit 3 or 4 test class could be
     *         identified, <code>false</code> otherwise.
     */
    protected boolean compilationUnitContainsJUnitTest(final CompilationUnit compilationUnit, final PackageDeclaration packageDescriptor) {
        final TestAnnotationResolver testPropertyResolver =
            new TestAnnotationResolver(new ClassNameResolver(packageDescriptor.getName().toString(), compilationUnit.getImports()));

        // find JUnit3 test cases.
        compilationUnit.accept(testPropertyResolver, null);

        // inspect method declarations for @Test annotation (JUnit4).
        if (!testPropertyResolver.junitTestFound()) {
            final List<TypeDeclaration> typeDeclarations = compilationUnit.getTypes();

            if (typeDeclarations != null) {
                for (final TypeDeclaration type : typeDeclarations) {
                    final List<BodyDeclaration> members = type.getMembers();

                    if (members != null) {
                        for (final BodyDeclaration body : members) {
                            body.accept(testPropertyResolver, null);
                        }
                    }
                }
            }
        }

        return testPropertyResolver.junitTestFound();
    }
}