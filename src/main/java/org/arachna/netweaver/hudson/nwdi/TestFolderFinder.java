/**
 * 
 */
package org.arachna.netweaver.hudson.nwdi;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import org.apache.commons.lang.StringUtils;
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
        for (final FileDescriptor source : getJavaSources(encoding, sourceFolder)) {
            try {
                String enc = StringUtils.isEmpty(encoding) ? "UTF-8" : encoding;
                final CompilationUnit compilationUnit = JavaParser.parse(source.getContent(), Charset.forName(enc));
                final Optional<PackageDeclaration> packageDescriptor = compilationUnit.getPackageDeclaration();

                if (packageDescriptor != null && compilationUnitContainsJUnitTest(compilationUnit, packageDescriptor.get())) {
                    return true;
                }
            }
            catch (final IOException e) {
                logger.log(Level.WARNING, source.getAbsolutePath() + ": " + e.getLocalizedMessage(), e);
            }
        }

        return false;
    }

    protected Collection<FileDescriptor> getJavaSources(final String encoding, final String sourceFolder) {
        final FileFinder finder = new FileFinder(new File(sourceFolder), ".*\\.java");
        final Collection<FileDescriptor> sources = new ArrayList<FileDescriptor>();

        for (final File file : finder.find()) {
            sources.add(new FileDescriptor(file));
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
     * @return <code>true</code> when a JUnit 3 or 4 test class could be identified, <code>false</code> otherwise.
     */
    protected boolean compilationUnitContainsJUnitTest(final CompilationUnit compilationUnit, final PackageDeclaration packageDescriptor) {
        final TestAnnotationResolver testPropertyResolver =
            new TestAnnotationResolver(new ClassNameResolver(packageDescriptor.getName().toString(), compilationUnit.getImports()));

        // find JUnit3 test cases.
        compilationUnit.accept(testPropertyResolver, null);

        // inspect method declarations for @Test annotation (JUnit4).
        if (!testPropertyResolver.junitTestFound()) {
            final List<? extends TypeDeclaration> typeDeclarations = compilationUnit.getTypes();

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

    public class FileDescriptor {
        private final File file;

        FileDescriptor(final File file) {
            this.file = file;
        }

        InputStream getContent() throws IOException {
            return new FileInputStream(file);
        }

        String getAbsolutePath() {
            return file.getAbsolutePath();
        }
    }
}
