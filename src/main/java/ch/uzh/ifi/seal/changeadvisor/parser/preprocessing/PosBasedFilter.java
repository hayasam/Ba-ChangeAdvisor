package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import edu.stanford.nlp.simple.Document;
import edu.stanford.nlp.simple.Sentence;
import edu.stanford.nlp.simple.Token;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * Created by alex on 20.07.2017.
 */
public class PosBasedFilter {

    private static final Set<String> NOUN_TAGS = ImmutableSet.of(
            "NN",   // Noun, singular or mass
            "NNS",  // Noun, plural
            "NNP",  // Proper noun, singular
            "NNPS", // Proper noun, plural
            "MD"    // Modals
    );

    private static final Set<String> VERB_TAGS = ImmutableSet.of(
            "VB",  // Verb, base form
            "VBD", // Verb, past tense
            "VBG", // Verb, gerund or present participle
            "VBN", // Verb, past participle
            "VBP", // Verb, non-3rd person singular present
            "VBZ"  // Verb, 3rd person singular present
    );

    /**
     * Filters text based on POS tags defined in this class.
     *
     * @param text text to filter.
     * @return filtered text by POS tags.
     */
    public List<String> filter(String text) {
        if (StringUtils.isEmpty(text)) {
            return ImmutableList.of();
        }

        Document doc = new Document(text);
        return doc.sentences().stream()
                .flatMap(this::sentenceToTokens)
                .filter(this::isNounOrVerb)
                .map(this::tokenToText)
                .collect(Collectors.toList());
    }

    private Stream<Token> sentenceToTokens(Sentence sentence) {
        return sentence.tokens().stream();
    }

    private String tokenToText(Token token) {
        return token.originalText();
    }

    private boolean isNounOrVerb(Token token) {
        final String posTag = token.posTag();
        return NOUN_TAGS.contains(posTag) || VERB_TAGS.contains(posTag);
    }
}


