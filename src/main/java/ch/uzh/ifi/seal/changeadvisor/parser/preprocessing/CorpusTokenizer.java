package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import java.util.List;

/**
 * Defines interface for a corpus tokenizer.
 * Created by alexanderhofmann on 16.07.17.
 */
public interface CorpusTokenizer {

    List<String> tokenize(String corpus);
}
