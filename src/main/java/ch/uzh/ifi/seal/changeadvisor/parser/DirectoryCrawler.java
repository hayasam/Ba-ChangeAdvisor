package ch.uzh.ifi.seal.changeadvisor.parser;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

/**
 * Crawls through directories in order to find all java files.
 * Created by alexanderhofmann on 13.07.17.
 */
public class DirectoryCrawler {

    private static final Logger logger = Logger.getLogger(DirectoryCrawler.class);

    public interface Filter {
        boolean filter(Path file);
    }

    private CrawlerFilter filter = CrawlerFilter.JAVA_FILTER;

    private List<Path> paths = new LinkedList<>();

    public List<Path> explore(Path root) {
        explore(root, 0);
        return paths;
    }

    private void explore(Path file, int depth) {
        if (Files.isDirectory(file)) {
            try {
                Files.list(file).forEach(p -> explore(p, depth + 1));
            } catch (IOException e) {
                logger.error("IOException while parsing folder: " + file.getFileName(), e);
            }
        } else if (filter.filter(file)) {
            paths.add(file);
        }
    }
}
