package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import ch.uzh.ifi.seal.changeadvisor.parser.BagOfWords;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.ClassBean;
import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemProcessor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * A processor to process the corpus of a Class.
 * A processor divides the work in tokenizer, steps and filters, as follows:
 * 1) Escape text. Optional.
 * 2) Tokenize text
 * 3) Apply filters.
 * 4) Apply processing steps.
 * <p>
 * To create a processor use the builder {@link CorpusProcessor.Builder}.
 *
 * @see CorpusProcessor.Builder
 * @see CorpusEscaper
 * @see CorpusTokenizer
 * @see ProcessingStep
 * Created by alexanderhofmann on 16.07.17.
 */
public class CorpusProcessor implements ItemProcessor<ClassBean, BagOfWords> {

    private static final Logger logger = Logger.getLogger(CorpusProcessor.class);

    private ProcessingStep firstStep;

    private CorpusEscaper escaper;

    private CorpusTokenizer tokenizer;

    private Predicate<String> filter;

    private CorpusProcessor() {
        /*
        Build instances using the builder. Makes sure each instance is always valid.
         */
    }

    public BagOfWords process(String fullyQualifiedClassName, String corpus) {
        String escaped = escaper.escape(corpus);
        List<String> tokenize = tokenizer.tokenize(escaped);
        Set<String> tokens = tokenize.stream().filter(filter).map(token -> firstStep.handle(token)).collect(Collectors.toSet());
        return new BagOfWords(fullyQualifiedClassName, tokens);
    }

    @Override
    public BagOfWords process(ClassBean item) throws Exception {
        final String fqcn = item.getFullyQualifiedClassName();
        final String corpus = item.getPublicCorpus();
        logger.debug("Converting corpus from component: " + fqcn);
        return process(fqcn, corpus);
    }

    public static class Builder {

        private CorpusProcessor processor;

        private Queue<ProcessingStep> steps;

        private List<Predicate<String>> filters = new ArrayList<>();

        private static final CorpusEscaper DO_NOTHING_ESCAPER = corpus -> corpus;

        private ProcessingStep finalStep = new ProcessingStep() {
            @Override
            public String handle(String text) {
                return text;
            }
        };

        public Builder(boolean escapeChars, CorpusTokenizer tokenizer) {
            processor = new CorpusProcessor();
            steps = new LinkedList<>();
            processor.escaper = getEscaper(escapeChars);
            processor.tokenizer = tokenizer;
        }

        private CorpusEscaper getEscaper(boolean escapeChars) {
            return escapeChars ? new EscapeSpecialCharacters() : DO_NOTHING_ESCAPER;
        }

        public Builder filter(Predicate<String> filter) {
            filters.add(filter);
            return this;
        }

        public Builder step(ProcessingStep step) {
            steps.add(step);
            return this;
        }

        public CorpusProcessor build() {
            setAllNextSteps();
            buildFilters();
            return processor;
        }

        private void setAllNextSteps() {
            ProcessingStep previous = steps.poll();
            processor.firstStep = previous;
            while (!steps.isEmpty()) {
                ProcessingStep next = steps.poll();
                previous.setNext(next);
                previous = next;
            }
            previous.setNext(finalStep);
        }

        private void buildFilters() {
            if (filters.isEmpty()) {
                processor.filter = (t) -> t.length() > 3;
            } else {
                processor.filter = filters.stream().reduce(Predicate::and).orElse(x -> true);
            }
        }
    }
}
