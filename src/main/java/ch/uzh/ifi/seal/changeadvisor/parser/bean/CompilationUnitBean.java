package ch.uzh.ifi.seal.changeadvisor.parser.bean;

import ch.uzh.ifi.seal.changeadvisor.parser.visitor.MethodVisitor;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.nodeTypes.NodeWithName;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Represents a compilation unit (essentialy a file).
 * Created by alexanderhofmann on 14.07.17.
 */
public final class CompilationUnitBean {

    private CompilationUnit compilationUnit;

    private CompilationUnitBean(CompilationUnit compilationUnit) {
        this.compilationUnit = compilationUnit;
    }

    public Optional<String> getPackageName() {
        Optional<PackageDeclaration> packageDeclaration = compilationUnit.getPackageDeclaration();
        return packageDeclaration.map(NodeWithName::getNameAsString);
    }

    public String getPublicCorpus() {
        return MethodVisitor.getCorpus(compilationUnit);
    }

    public static CompilationUnitBean fromPath(Path path) throws IOException {
        CompilationUnit cu = JavaParser.parse(path);
        return new CompilationUnitBean(cu);
    }
}
