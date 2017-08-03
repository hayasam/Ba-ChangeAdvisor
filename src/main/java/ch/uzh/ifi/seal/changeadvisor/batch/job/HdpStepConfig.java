package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicClustering;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicClusteringResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicWriter;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TransformedFeedbackReader;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by alex on 24.07.2017.
 */
@Component
public class HdpStepConfig {

    private static final String STEP_NAME = "documents_clustering";

    private final StepBuilderFactory stepBuilderFactory;

    private final MongoTemplate mongoTemplate;

    private final TransformedFeedbackReader transformedFeedbackReader;

    private final TopicWriter topicWriter;

    @Autowired
    public HdpStepConfig(StepBuilderFactory stepBuilderFactory, MongoTemplate mongoTemplate, TransformedFeedbackReader transformedFeedbackReader, TopicWriter topicWriter) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.mongoTemplate = mongoTemplate;
        this.transformedFeedbackReader = transformedFeedbackReader;
        this.topicWriter = topicWriter;
    }

    @Bean
    public Step documentsClustering() {
        return stepBuilderFactory.get(STEP_NAME)
                .<List<TransformedFeedback>, TopicClusteringResult>chunk(10)
                .reader(transformedFeedbackReader)
                .processor(topicClustering())
                .writer(topicWriter)
                .build();
    }

    @Bean
    public TopicClustering topicClustering() {
        return new TopicClustering(100);
    }
}
