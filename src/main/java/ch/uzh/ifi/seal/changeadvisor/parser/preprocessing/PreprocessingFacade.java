package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import com.google.common.base.Splitter;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Pre-processing facade to simplify usage.
 * Created by alex on 14.07.2017.
 */
public class PreprocessingFacade {

    public static Set<String> preprocess(String text) {
        text = new EscapeSpecialCharacters().escape(text);
        List<String> split = Splitter.on(' ').omitEmptyStrings().trimResults().splitToList(text);

        return split.stream()
                .map(ComposedIdentifierSplitter::split)
                .flatMap(Collection::stream)
                .map(String::toLowerCase)
                .filter(StopWordFilter::isNotStopWord)
                .map(Stemmer::stem)
                .filter(token -> token.length() > 3)
                .collect(Collectors.toSet());
    }
}
