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

    public FeedbackProcessor(CorpusProcessor corpusProcessor) {
        this.corpusProcessor = corpusProcessor;
    }

    @Override
    public TransformedFeedback process(ArdocResult item) throws Exception {
        Set<String> bag = corpusProcessor.transform(item.getResult().getSentence());
        if (bag.isEmpty()) {
            return null;
        }
        return new TransformedFeedback(item, bag);
    }
}
