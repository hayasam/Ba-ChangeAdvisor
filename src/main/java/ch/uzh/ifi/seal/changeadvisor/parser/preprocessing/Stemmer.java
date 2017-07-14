package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import org.tartarus.snowball.ext.PorterStemmer;

/**
 * Porter Stemmer.
 * Created by alex on 14.07.2017.
 */
class Stemmer {

    private static PorterStemmer stemmer = new PorterStemmer();

    static String stem(String token) {
        stemmer.setCurrent(token);
        stemmer.stem();
        return stemmer.getCurrent();
    }
}
