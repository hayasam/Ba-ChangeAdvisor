package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.TransformedFeedback;
import org.apache.log4j.Logger;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by alex on 20.07.2017.
 */
public class Feedback {

    private static final Logger logger = Logger.getLogger(Feedback.class);

    private static final int MIN_LENGTH = 3;

    private ArdocResult document;

    private String text;

    private Set<AnnotatedToken> tokens;

    private SpellChecker spellChecker;

    private ContractionsExpander contractionsExpander;

    public Feedback(ArdocResult document) {
        this.document = document;
        this.text = document.getResult().getSentence();
    }

    public void autoCorrect() {
        if (spellChecker != null) {
            text = spellChecker.correct(text);
        } else {
            logger.warn("Trying to correct spelling, but no spellChecker set!");
        }
    }

    public void expandContractions() {
        if (contractionsExpander != null) {
            text = contractionsExpander.expand(text);
        } else {
            logger.warn("Trying to expand contractions, but no expander set!");
        }
    }

    public void tokenize(boolean shouldFilterPos) {
        Annotator annotator = new Annotator();
        tokens = annotator.annotate(text, shouldFilterPos);
    }

    public void singularize() {
        tokens.forEach(AnnotatedToken::singularize);
    }

    public void removeStopWords() {
        tokens.removeIf(AnnotatedToken::isStopWord);
    }

    public void stem() {
        tokens.forEach(AnnotatedToken::stem);
    }

    public void removeShortTokens() {
        removeShortTokens(MIN_LENGTH);
    }

    public void removeShortTokens(int minLength) {
        tokens.removeIf(token -> token.length() < minLength);
    }

    public boolean isTooShort() {
        return isTooShort(MIN_LENGTH);
    }

    public boolean isTooShort(int minLength) {
        return tokens.size() < 3;
    }

    public TransformedFeedback transform() {
        autoCorrect();
        expandContractions();
        tokenize(true);
        singularize();
        removeStopWords();
        stem();
        removeShortTokens();
        Set<String> tokens = this.tokens.stream().map(AnnotatedToken::getToken).collect(Collectors.toSet());
        TransformedFeedback result = new TransformedFeedback(document, tokens);
        return isTooShort() ? null : result;
    }

    public static class Builder {

        private Feedback feedback;

        public Builder(ArdocResult document) {
            feedback = new Feedback(document);
        }

        public Builder withSpellChecker(SpellChecker spellChecker) {
            feedback.spellChecker = spellChecker;
            return this;
        }

        public Builder withContractionExpander(ContractionsExpander contractionExpander) {
            feedback.contractionsExpander = contractionExpander;
            return this;
        }

        public Feedback build() {
            return feedback;
        }

        public TransformedFeedback buildAndTransform() {
            return feedback.transform();
        }
    }
}
