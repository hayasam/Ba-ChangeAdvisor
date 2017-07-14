package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Filter for stopwords. Uses dictionary files located at /resources/nlp for filtering.
 * Created by alex on 14.07.2017.
 */
class StopWordFilter {

    private static final Logger logger = Logger.getLogger(StopWordFilter.class);

    private static Set<String> stopwords = ImmutableSet.of();

    private static Set<String> programmingStopwords = ImmutableSet.of();

    static {
        try {
            stopwords = ImmutableSet.copyOf(FileUtils.readLines(Paths.get("src/main/resources/nlp/stopwords").toFile(), "utf8"));
            programmingStopwords = ImmutableSet.copyOf(FileUtils.readLines(Paths.get("src/main/resources/nlp/code_stopwords").toFile(), "utf8"));
        } catch (IOException e) {
            logger.info("Failed to read stopwords file! Not going to filter stopwords!", e);
        }
    }

    static boolean isNotStopWord(String token) {
        return isNotNormalStopWord(token) && isNotProgrammingStopWord(token);
    }

    private static boolean isNotNormalStopWord(String token) {
        return stopwords.isEmpty() || !stopwords.contains(token);
    }

    private static boolean isNotProgrammingStopWord(String token) {
        return programmingStopwords.isEmpty() || !programmingStopwords.contains(token);
    }
}
