package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import com.google.common.collect.Sets;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * NLP processor for text. Use builder {@link CorpusProcessor.Builder} to configure its steps.
 * Created by alex on 20.07.2017.
 */
public class CorpusProcessor {

    private SpellChecker spellChecker;

    private ContractionsExpander contractionsExpander;

    private boolean shouldEscapeCharacters;

    private boolean shouldSingularize;

    private boolean shouldRemoveStopWords;

    private boolean shouldStem;

    private boolean shouldRemoveShortTokens;

    private boolean shouldLowerCase;

    private int minLength;

    private boolean posFilter;

    private ComposedIdentifierSplitter composedIdentifierSplitter;

    private EscapeSpecialCharacters escapeSpecialCharacters;

    private Annotator annotator = new Annotator();

    private CorpusProcessor() {
    }

    /**
     * Takes text as a bag of words and processes it based on the steps defined during construction.
     *
     * @param bag bag of words.
     * @return transformed bag of words.
     */
    public Set<String> transform(Collection<String> bag) {
        Assert.notNull(bag, "Text to transform must not be null.");
        return transform(String.join(" ", bag));
    }

    /**
     * Takes text as a string and processes it based on the steps defined during construction.
     *
     * @param text text as string.
     * @return transformed bag of words.
     */
    public Set<String> transform(String text) {
        Assert.notNull(text, "Text to transform must not be null.");

        if (text.isEmpty()) {
            return Sets.newHashSet(text);
        }

        if (composedIdentifierSplitter != null) {
            text = composedIdentifierSplitter.split(text);
        }

        if (contractionsExpander != null) {
            text = contractionsExpander.expand(text);
        }

        if (shouldEscapeCharacters) {
            text = escapeSpecialCharacters.escape(text);
        }

        if (spellChecker != null) {
            text = spellChecker.correct(text);
        }


        if (shouldLowerCase) {
            text = text.toLowerCase();
        }

        Set<AnnotatedToken> tokens = tokenize(text, posFilter);

        tokenAutoCorrect(tokens);

        if (shouldSingularize) {
            singularize(tokens);
        }

        if (shouldRemoveStopWords) {
            removeStopWords(tokens);
        }

        if (shouldStem) {
            stem(tokens);
        }

        if (shouldRemoveShortTokens) {
            tokens.removeIf(this::isTooShort);
        }

        return tokens.stream().map(AnnotatedToken::getToken).collect(Collectors.toSet());
    }

    private void tokenAutoCorrect(Set<AnnotatedToken> tokens) {
        if (spellChecker != null) {
            tokens.forEach(token -> token.setToken(spellChecker.correct(token.getToken())));
        }
    }

    private Set<AnnotatedToken> tokenize(String text, boolean shouldFilterPos) {
        return annotator.annotate(text, shouldFilterPos);
    }

    private void singularize(Set<AnnotatedToken> tokens) {
        tokens.forEach(AnnotatedToken::singularize);
    }

    private void removeStopWords(Set<AnnotatedToken> tokens) {
        tokens.removeIf(AnnotatedToken::isStopWord);
    }

    private void stem(Set<AnnotatedToken> tokens) {
        tokens.forEach(AnnotatedToken::stem);
    }

    private boolean isTooShort(AnnotatedToken token) {
        return token.length() < minLength;
    }

    /**
     * Builder class for {@link CorpusProcessor}.
     */
    public static class Builder {

        private CorpusProcessor corpusProcessor;

        public Builder() {
            corpusProcessor = new CorpusProcessor();
        }

        /**
         * Will lower case.
         *
         * @return this builder for chaining.
         */
        public Builder lowerCase() {
            corpusProcessor.shouldLowerCase = true;
            return this;
        }

        /**
         * Will escape special chars.
         *
         * @return this builder for chaining.
         * @see EscapeSpecialCharacters
         */
        public Builder escapeSpecialChars() {
            corpusProcessor.shouldEscapeCharacters = true;
            corpusProcessor.escapeSpecialCharacters = new EscapeSpecialCharacters();
            return this;
        }

        /**
         * Will split composed identifiers (camel case, snake case, ...)
         *
         * @return this builder for chaining.
         * @see ComposedIdentifierSplitter
         */
        public Builder withComposedIdentifierSplit() {
            corpusProcessor.composedIdentifierSplitter = new ComposedIdentifierSplitter();
            return this;
        }

        /**
         * Will run spell checking with provided SpellChecker.
         *
         * @param spellChecker spellchecker to use.
         * @return this builder for chaining.
         * @see SpellChecker
         */
        public Builder withAutoCorrect(SpellChecker spellChecker) {
            Assert.notNull(spellChecker, "Spell checker must not be null.");
            corpusProcessor.spellChecker = spellChecker;
            return this;
        }

        /**
         * Will expand contractions (It's -> It is, etc.).
         *
         * @return this builder for chaining.
         * @see ContractionsExpander
         */
        public Builder withContractionExpander() {
            corpusProcessor.contractionsExpander = new ContractionsExpander();
            return this;
        }

        /**
         * Will singularize each token. Each token will be transformed to its singular form e.g. (cars -> car).
         *
         * @return this builder for chaining.
         */
        public Builder singularize() {
            corpusProcessor.shouldSingularize = true;
            return this;
        }

        /**
         * Will remove stop words.
         *
         * @return this builder for chaining.
         */
        public Builder removeStopWords() {
            corpusProcessor.shouldRemoveStopWords = true;
            return this;
        }

        /**
         * Will stem each token.
         *
         * @return this builder for chaining.
         */
        public Builder stem() {
            corpusProcessor.shouldStem = true;
            return this;
        }

        /**
         * Will remove tokens shorter than minLength.
         *
         * @param minLength minimum length of a token in order for this to be kept.
         *                  If minLength < 1 will throw {@link IllegalArgumentException}.
         * @return this builder for chaining.
         */
        public Builder removeTokensShorterThan(int minLength) {
            if (minLength < 1) {
                throw new IllegalArgumentException(String.format("Min length must be > 1. Was %d", minLength));
            }
            corpusProcessor.shouldRemoveShortTokens = true;
            corpusProcessor.minLength = minLength;
            return this;
        }

        /**
         * Will filter tokens based on their Part-Of-Speech tag.
         *
         * @return this builder for chaining.
         */
        public Builder posFilter() {
            corpusProcessor.posFilter = true;
            return this;
        }

        /**
         * Builds processor.
         *
         * @return the built processor.
         */
        public CorpusProcessor build() {
            return corpusProcessor;
        }
    }
}
