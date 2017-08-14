package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by alex on 20.07.2017.
 */
public class CorpusProcessor {

    private String text;

    private Set<AnnotatedToken> tokens;

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

    public Set<String> transform(Collection<String> bag) {
        return transform(String.join(" ", bag));
    }

    public Set<String> transform(String text) {

        if (composedIdentifierSplitter != null) {
            text = composedIdentifierSplitter.split(text);
        }

        if (shouldEscapeCharacters) {
            text = escapeSpecialCharacters.escape(text);
        }

        if (shouldLowerCase) {
            text = text.toLowerCase();
        }

        if (spellChecker != null) {
            text = spellChecker.correct(text);
        }

        this.text = text;

        expandContractions();

        tokenize(posFilter);

        tokenAutoCorrect();

        if (shouldSingularize) {
            singularize();
        }

        if (shouldRemoveStopWords) {
            removeStopWords();
        }

        if (shouldStem) {
            stem();
        }

        if (shouldRemoveShortTokens) {
            removeShortTokens();
        }

        return this.tokens.stream().map(AnnotatedToken::getToken).collect(Collectors.toSet());
    }

    public void autoCorrect() {
        if (spellChecker != null) {
            text = spellChecker.correct(text);
        }
    }

    private void tokenAutoCorrect() {
        if (spellChecker != null) {
            tokens.forEach(token -> token.setToken(spellChecker.correct(token.getToken())));
        }
    }

    public void expandContractions() {
        if (contractionsExpander != null) {
            text = contractionsExpander.expand(text);
        }
    }

    public void tokenize(boolean shouldFilterPos) {
        tokens = annotator.annotate(text, shouldFilterPos);
    }

    private void singularize() {
        tokens.forEach(AnnotatedToken::singularize);
    }

    private void removeStopWords() {
        tokens.removeIf(AnnotatedToken::isStopWord);
    }

    private void stem() {
        tokens.forEach(AnnotatedToken::stem);
    }

    private void removeShortTokens() {
        tokens.removeIf(token -> token.length() < minLength);
    }

    public static class Builder {

        private CorpusProcessor corpusProcessor;

        public Builder() {
            corpusProcessor = new CorpusProcessor();
        }

        public Builder lowerCase() {
            corpusProcessor.shouldLowerCase = true;
            return this;
        }

        public Builder escapeSpecialChars() {
            corpusProcessor.shouldEscapeCharacters = true;
            corpusProcessor.escapeSpecialCharacters = new EscapeSpecialCharacters();
            return this;
        }

        public Builder withComposedIdentifierSplit(ComposedIdentifierSplitter splitter) {
            corpusProcessor.composedIdentifierSplitter = splitter;
            return this;
        }

        public Builder withAutoCorrect(SpellChecker spellChecker) {
            corpusProcessor.spellChecker = spellChecker;
            return this;
        }

        public Builder withContractionExpander(ContractionsExpander contractionExpander) {
            corpusProcessor.contractionsExpander = contractionExpander;
            return this;
        }

        public Builder singularize() {
            corpusProcessor.shouldSingularize = true;
            return this;
        }

        public Builder removeStopWords() {
            corpusProcessor.shouldRemoveStopWords = true;
            return this;
        }

        public Builder stem() {
            corpusProcessor.shouldStem = true;
            return this;
        }

        public Builder removeTokensShorterThan(int minLength) {
            corpusProcessor.shouldRemoveShortTokens = true;
            corpusProcessor.minLength = minLength;
            return this;
        }

        public Builder posFilter() {
            corpusProcessor.posFilter = true;
            return this;
        }

        public CorpusProcessor build() {
            return corpusProcessor;
        }
    }
}
