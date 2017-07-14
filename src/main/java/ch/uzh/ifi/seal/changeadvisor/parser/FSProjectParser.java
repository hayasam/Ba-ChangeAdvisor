package ch.uzh.ifi.seal.changeadvisor.parser;

import ch.uzh.ifi.seal.changeadvisor.parser.bean.CompilationUnitBean;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.PackageBean;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File System parser for java projects.
 * Created by alexanderhofmann on 14.07.17.
 */
public class FSProjectParser {

    private static final Logger logger = Logger.getLogger(FSProjectParser.class);

    /**
     * Given a project root it parses the various packages and compilation units.
     *
     * @param projectRoot {@link Path} to project root.
     * @return list of package beans {@link PackageBean}.
     */
    public List<PackageBean> parse(Path projectRoot) {
        DirectoryCrawler crawler = new DirectoryCrawler();
        List<Path> projectFiles = crawler.explore(projectRoot);
        Map<String, PackageBean> packageMap = new HashMap<>();

        projectFiles.forEach(file -> {
            try {
                CompilationUnitBean compilationUnit = CompilationUnitBean.fromPath(file);
                final String packageName = compilationUnit.getPackageName().orElse("default");

                PackageBean packageBean = packageMap.getOrDefault(packageName, new PackageBean(packageName));
                packageBean.addCompilationUnit(compilationUnit);
                packageMap.putIfAbsent(packageName, packageBean);

            } catch (IOException e) {
                logger.error(String.format("Failed to parse file at path: %s", file.toString()), e);
            }
        });

        return new ArrayList<>(packageMap.values());
    }

}
