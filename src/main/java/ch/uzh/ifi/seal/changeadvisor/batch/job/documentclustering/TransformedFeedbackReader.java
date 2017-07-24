package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.FeedbackWriter;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by alex on 24.07.2017.
 */
@Component
public class TransformedFeedbackReader implements ItemReader<List<TransformedFeedback>> {

    private static final Logger logger = Logger.getLogger(TransformedFeedbackReader.class);

    private MongoItemReader<TransformedFeedback> reader;

    private TopicAssignmentRepository assignmentRepository;

    private TopicRepository topicRepository;

    private TransformedFeedbackRepository feedbackRepository;

    private List<TransformedFeedback> allFeedbacks;

    private boolean hasRead = false;

    @Autowired
    public TransformedFeedbackReader(MongoTemplate mongoTemplate, TopicAssignmentRepository assignmentRepository, TopicRepository topicRepository, TransformedFeedbackRepository feedbackRepository) {
        this.assignmentRepository = assignmentRepository;
        this.topicRepository = topicRepository;
        this.feedbackRepository = feedbackRepository;
        reader = new MongoItemReader<>();
        reader.setTemplate(mongoTemplate);
        reader.setCollection(FeedbackWriter.COLLECTION_NAME);
        reader.setQuery("{}");
        Map<String, Sort.Direction> sort = new HashMap<>();
        sort.put("_id", Sort.Direction.ASC);
        reader.setSort(sort);
        reader.setTargetType(TransformedFeedback.class);
    }


    @Override
    public List<TransformedFeedback> read() throws Exception {
        if (hasRead) {
            return null;
        }
        allFeedbacks = feedbackRepository.findAll();
        hasRead = true;
        logger.info(allFeedbacks.size());
        return allFeedbacks;
    }
}
