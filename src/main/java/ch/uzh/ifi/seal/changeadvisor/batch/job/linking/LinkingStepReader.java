package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.*;
import org.apache.log4j.Logger;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinkingStepReader implements ItemReader<TopicClusteringResult> {

    private static final Logger logger = Logger.getLogger(LinkingStepReader.class);

    private final TopicRepository topicRepository;

    private final TopicAssignmentRepository topicAssignmentRepository;

    private boolean hasRead = false;

    @Autowired
    public LinkingStepReader(TopicRepository topicRepository, TopicAssignmentRepository topicAssignmentRepository) {
        this.topicRepository = topicRepository;
        this.topicAssignmentRepository = topicAssignmentRepository;
    }

    @Override
    public TopicClusteringResult read() throws Exception {
        if (hasRead) {
            return null;
        }
        TopicClusteringResult topicClusteringResult = readFromRepository();
        hasRead = true;
        logger.info(String.format("Read from repositories %d topics and %d assignments",
                topicClusteringResult.topicSize(), topicClusteringResult.assignmentSize()));
        return topicClusteringResult;
    }

    private TopicClusteringResult readFromRepository() {
        List<Topic> topics = topicRepository.findAll();
        List<TopicAssignment> assignments = topicAssignmentRepository.findAll();
        return new TopicClusteringResult(topics, assignments);
    }
}
