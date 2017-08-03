package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.*;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import com.google.common.collect.Sets;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

/**
 * Created by alex on 24.07.2017.
 */
@Component
public class DocumentClusteringStepConfig {

    private static final String STEP_NAME = "documents_clustering";

    private final StepBuilderFactory stepBuilderFactory;

    private final TopicWriter topicWriter;

    private final TransformedFeedbackReader mongoFeedbackReader;

    private static final int DEFAUL_MAX_ITERATIONS = 100;

    private int maxIterations = -1;

    @Autowired
    public DocumentClusteringStepConfig(StepBuilderFactory stepBuilderFactory, TopicWriter topicWriter, TransformedFeedbackReader mongoFeedbackReader) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.topicWriter = topicWriter;
        this.mongoFeedbackReader = mongoFeedbackReader;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    @Bean
    public Step documentsClustering() {
        return stepBuilderFactory.get(STEP_NAME)
                .<List<TransformedFeedback>, TopicClusteringResult>chunk(10)
                .reader(mongoFeedbackReader)
                .processor(topicClustering())
                .writer(topicWriter)
                .build();
    }

    @Bean
    public TopicClustering topicClustering() {
        return new TopicClustering(maxIterations < 1 ? DEFAUL_MAX_ITERATIONS : maxIterations);
    }

    @Bean
    public FlatFileTransformedFeedbackReader flatFileTransformedFeedbackReader() {
        final String filePath = "test_files_parser/transformed_feedback/feedback.csv";
        final Set<String> inputCategories = Sets.newHashSet("FEATURE REQUEST", "PROBLEM DISCOVERY");
        return new FlatFileTransformedFeedbackReader(filePath, inputCategories);
    }
}
