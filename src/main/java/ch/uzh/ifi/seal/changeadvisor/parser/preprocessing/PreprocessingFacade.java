package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import com.google.common.base.Splitter;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Pre-processing facade to simplify usage.
 * Created by alex on 14.07.2017.
 */
public class PreprocessingFacade {

    private static final ComposedIdentifierSplitter composedIdSplitter = new ComposedIdentifierSplitter();

    public static Set<String> preprocess(String text) {
        text = new EscapeSpecialCharacters().escape(text);
        text = composedIdSplitter.split(text);
        List<String> split = Splitter.on(' ').omitEmptyStrings().trimResults().splitToList(text);

        return split.stream()
                .map(String::toLowerCase)
                .filter(StopWordFilter::isNotStopWord)
                .map(s -> Stemmer.stem(s, 3))
                .filter(token -> token.length() > 3)
                .collect(Collectors.toSet());
    }
}
