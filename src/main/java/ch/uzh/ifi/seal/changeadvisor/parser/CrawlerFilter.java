package ch.uzh.ifi.seal.changeadvisor.parser;

import java.nio.file.Path;

/**
 * Created by alexanderhofmann on 13.07.17.
 */
@FunctionalInterface
public interface CrawlerFilter {

    boolean filter(Path file);

}
