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
import java.io.IOException;
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
     * @return <code>true</code> when there are sources in the given folder containing unit tests, <code>false</code> else.
     */
    boolean isTestFolder(final String encoding, final String sourceFolder) {
        final FileFinder finder = new FileFinder(new File(sourceFolder), ".*\\.java");

        for (final File file : finder.find()) {
            try {
                final CompilationUnit compilationUnit = JavaParser.parse(file, encoding);
                final PackageDeclaration packageDescriptor = compilationUnit.getPackage();

                if (packageDescriptor != null) {
                    if (compilationUnitContainsJUnitTest(compilationUnit, packageDescriptor)) {
                        return true;
                    }
                }
            }
            catch (final ParseException e) {
                logger.log(Level.WARNING, e.getLocalizedMessage(), e);
            }
            catch (final IOException e) {
                logger.log(Level.WARNING, e.getLocalizedMessage(), e);
            }
        }

        return false;
    }

    /**
     * Determine whether the given compilation unit contains JUnit tests.
     * 
     * @param compilationUnit
     *            the compilation unit to test for JUnit tests.
     * @param packageDescriptor
     *            descriptor of package of compilation unit.
     * @return <code>true</code> when a JUnit 3 or 4 test class could be identified, <code>false</code> otherwise.
     */
    protected boolean compilationUnitContainsJUnitTest(final CompilationUnit compilationUnit, final PackageDeclaration packageDescriptor) {
        final TestPropertyResolver testPropertyResolver =
            new TestPropertyResolver(new ClassNameResolver(packageDescriptor.getName().toString(), compilationUnit.getImports()));

        // find JUnit3 test cases.
        compilationUnit.accept(testPropertyResolver, null);

        // inspect method declarations for @Test annotation (JUnit4).
        if (!testPropertyResolver.junitTestFound()) {
            for (final TypeDeclaration type : compilationUnit.getTypes()) {
                for (final BodyDeclaration body : type.getMembers()) {
                    body.accept(testPropertyResolver, null);
                }
            }
        }

        return testPropertyResolver.junitTestFound();
    }
}