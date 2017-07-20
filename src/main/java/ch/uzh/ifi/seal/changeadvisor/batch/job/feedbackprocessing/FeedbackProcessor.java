package ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.ContractionsExpander;
import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.EnglishSpellChecker;
import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.Feedback;
import org.springframework.batch.item.ItemProcessor;

/**
 * Created by alex on 20.07.2017.
 */
public class FeedbackProcessor implements ItemProcessor<ArdocResult, TransformedFeedback> {

    @Override
    public TransformedFeedback process(ArdocResult item) throws Exception {
        return new Feedback.Builder(item)
                .withContractionExpander(new ContractionsExpander())
                .withSpellChecker(new EnglishSpellChecker())
                .buildAndTransform();
    }
}
