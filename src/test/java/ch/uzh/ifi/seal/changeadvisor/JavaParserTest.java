package ch.uzh.ifi.seal.changeadvisor;


import ch.uzh.ifi.seal.changeadvisor.parser.DirectoryCrawler;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Testing java parsers library.
 * Created by alexanderhofmann on 13.07.17.
 */
public class JavaParserTest {

    private static final Logger logger = LoggerFactory.getLogger(JavaParserTest.class);


    @Test
    public void javaParser() throws Exception {
        logger.info(getClass().getSimpleName() + ".java");
        // creates an input stream for the file to be parsed
//        FileInputStream in = new FileInputStream("/Users/alexanderhofmann/Dropbox/UZH/Bsc/Ba/changeadvisor_input/sources/com.frostwire.android/common/src/main/java/com/frostwire/bittorrent/BTDownloadItem.java");
        DirectoryCrawler crawler = new DirectoryCrawler();
        List<Path> paths = crawler.explore(Paths.get("/Users/alexanderhofmann/Dropbox/UZH/Bsc/Ba/changeadvisor_input/sources/com.frostwire.android/common/src/main/java/com/frostwire/bittorrent/BTDownload.java"));
        logger.info(paths.size() + "");

//        Path file = paths.get(1);
        CompilationUnit cu = JavaParser.parse(paths.get(0));

        new ClassVisitor().visit(cu, null);


        // parse the file
//        CompilationUnit cu = JavaParser.parse(in);
        // visit and print the methods names
//        new MethodVisitor().visit(cu, null);


        // prints the resulting compilation unit to default system output
//        logger.info(cu.toString());
//        logger.info(cu.getPackageDeclaration().get().getName());
    }

    private static class MethodVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(MethodDeclaration n, Void arg) {
            /* here you can access the attributes of the method.
             this method will be called for all methods in this
             CompilationUnit, including inner class methods */
            if (n.isPublic()) {
                logger.info("\n" + n + "\n-----");
            } else {
                logger.info("ITS PRIVATE: " + n.getNameAsString());
            }
            super.visit(n, arg);
        }
    }

    private static class ClassVisitor extends VoidVisitorAdapter<Void> {

        @Override
        public void visit(ClassOrInterfaceDeclaration n, Void arg) {
            logger.info(n.toString());
            n.getMethods().forEach(m -> logger.info(m.toString()));
            super.visit(n, arg);
        }
    }
}
