package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import com.google.common.collect.Sets;
import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by alex on 24.07.2017.
 */
@Component
public class TransformedFeedbackReader implements ItemReader<List<TransformedFeedback>> {

    private static final Logger logger = Logger.getLogger(TransformedFeedbackReader.class);

    private TransformedFeedbackRepository feedbackRepository;

    private boolean hasRead = false;

    @Autowired
    public TransformedFeedbackReader(MongoTemplate mongoTemplate, TransformedFeedbackRepository feedbackRepository) {
        this.feedbackRepository = feedbackRepository;
    }


    @Override
    public List<TransformedFeedback> read() throws Exception {
        if (hasRead) {
            return null;
        }

        List<TransformedFeedback> allFeedbacks = feedbackRepository.findAllByArdocResultCategoryIn(Sets.newHashSet("FEATURE REQUEST", "PROBLEM DISCOVERY"));
        hasRead = true;
        logger.info(allFeedbacks.size());
        return allFeedbacks;
    }
}
