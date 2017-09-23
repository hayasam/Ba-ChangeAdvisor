package ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.preprocessing.CorpusProcessor;
import org.springframework.batch.item.ItemProcessor;

import java.util.Set;

/**
 * Created by alex on 20.07.2017.
 */
public class FeedbackProcessor implements ItemProcessor<ArdocResult, TransformedFeedback> {

    private CorpusProcessor corpusProcessor;

    private final int THRESHOLD;

    public FeedbackProcessor(CorpusProcessor corpusProcessor, int threshold) {
        this.corpusProcessor = corpusProcessor;
        THRESHOLD = threshold;
    }

    @Override
    public TransformedFeedback process(ArdocResult item) throws Exception {
        Set<String> bag = corpusProcessor.transform(item.getSentence());
        if (bag.size() < THRESHOLD) {
            return null;
        }
        return new TransformedFeedback(item, bag);
    }
}
