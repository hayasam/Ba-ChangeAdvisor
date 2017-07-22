package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import com.google.common.collect.ImmutableSet;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

/**
 * Created by alex on 20.07.2017.
 */
public class Annotator {

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

    private StanfordCoreNLP pipeline;

    private ComposedIdentifierSplitter composedIdentifierSplitter;

    public Annotator() {
        Properties props = new Properties();
        props.setProperty("annotators", "tokenize, ssplit, pos, lemma");
        pipeline = new StanfordCoreNLP(props);
        composedIdentifierSplitter = new ComposedIdentifierSplitter();
    }

    /**
     * Filters text based on POS tags defined in this class.
     *
     * @param text            text to annotate.
     * @param shouldFilterPos whether to filter while processing, keeping only nouns and verbs.
     * @return filtered text by POS tags.
     */
    public Set<AnnotatedToken> annotate(String text, boolean shouldFilterPos) {
        if (StringUtils.isEmpty(text)) {
            return new HashSet<>();
        }

        Annotation document = new Annotation(text);
        pipeline.annotate(document);

        Set<AnnotatedToken> tokens = new HashSet<>();

        List<CoreLabel> coreLabels = document.get(CoreAnnotations.TokensAnnotation.class);
        for (CoreLabel token : coreLabels) {
            String word = token.get(CoreAnnotations.TextAnnotation.class);
            String pos = token.get(CoreAnnotations.PartOfSpeechAnnotation.class);
            String lemma = token.get(CoreAnnotations.LemmaAnnotation.class);
            boolean isStopWord = !StopWordFilter.isNotStopWord(word);

            if (shouldFilterPos) {
                if (isNounOrVerb(pos)) {
                    tokens.add(new AnnotatedToken(word, pos, lemma, isStopWord));
                }
            } else {
                tokens.add(new AnnotatedToken(word, pos, lemma, isStopWord));
            }
        }
        return tokens;
    }

    private boolean isNounOrVerb(String posTag) {
        return NOUN_TAGS.contains(posTag) || VERB_TAGS.contains(posTag);
    }
}


