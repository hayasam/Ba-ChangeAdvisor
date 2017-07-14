package ch.uzh.ifi.seal.changeadvisor.parser.bean;

import ch.uzh.ifi.seal.changeadvisor.parser.visitor.ClassVisitor;
import ch.uzh.ifi.seal.changeadvisor.parser.visitor.MethodVisitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

/**
 * Represents a compilation unit (essentialy a file).
 * Created by alexanderhofmann on 14.07.17.
 */
public final class CompilationUnitBean {

    private CompilationUnit compilationUnit;

    private List<ClassBean> classes;

    private CompilationUnitBean(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
        classes = parseCompilationUnitForClasses();
    }

    public static CompilationUnitBean fromPath(Path path) throws IOException {
        CompilationUnit cu = JavaParser.parse(path);
        return new CompilationUnitBean(cu);
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public String getPackageName() {
        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
        return packageDeclaration.map(NodeWithName::getNameAsString).orElse("default");
    }

    public String getPublicCorpus() {
        return MethodVisitor.getCorpus(compilationUnit);
    }

    public List<ClassBean> getClasses() {
        return classes;
    }

    private List<ClassBean> parseCompilationUnitForClasses() {
        return ClassVisitor.getClasses(getPackageName(), compilationUnit);
    }
}
