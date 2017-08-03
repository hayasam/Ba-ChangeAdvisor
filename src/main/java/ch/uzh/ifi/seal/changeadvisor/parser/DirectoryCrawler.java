package ch.uzh.ifi.seal.changeadvisor.parser;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Crawls through directories in order to find all java files.
 * Created by alexanderhofmann on 13.07.17.
 */
public class DirectoryCrawler {

    private static final Logger logger = Logger.getLogger(DirectoryCrawler.class);
    private CrawlerFilter filter = file -> file.toString().contains(".java");
    private List<Path> paths = new LinkedList<>();

    public List<Path> explore(Path root) {
        explore(root, 0);
        return paths;
    }

    private void explore(Path file, int depth) {
        if (isDirectory(file)) {
            exploreDirectory(file, depth);
        } else if (filter.filter(file)) {
            paths.add(file);
        }
    }

    private void exploreDirectory(Path directory, int depth) {
        try (Stream<Path> stream = Files.list(directory)) {
            stream.forEach(filePath -> explore(filePath, depth + 1));
        } catch (IOException e) {
            logger.error("IOException while parsing directory: " + directory.getFileName(), e);
        }
    }

    private boolean isDirectory(Path file) {
        return Files.isDirectory(file);
    }
}
