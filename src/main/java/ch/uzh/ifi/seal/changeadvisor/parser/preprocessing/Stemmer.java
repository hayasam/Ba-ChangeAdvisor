package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import org.tartarus.snowball.ext.PorterStemmer;

/**
 * Porter Stemmer.
 * Created by alex on 14.07.2017.
 */
public class Stemmer extends ProcessingStep {

    private static PorterStemmer stemmer = new PorterStemmer();

    public static final int MINIMUM_WORD_LENGTH = 2;

    /**
     * Minimum word length to apply stemming.
     */
    private final int minWordLength;

    public Stemmer(int minWordLength) {
        this.minWordLength = minWordLength;
    }

    /**
     * Stems the given token using Porter's stemmer.
     *
     * @param token token to stem.
     * @return stemmed token.
     */
    static String stem(String token) {
        stemmer.setCurrent(token);
        stemmer.stem();
        return stemmer.getCurrent();
    }

    @Override
    public String handle(String text) {
        if (text.length() > minWordLength) {
            text = stem(text);
        }
        return next(text);
    }
}
