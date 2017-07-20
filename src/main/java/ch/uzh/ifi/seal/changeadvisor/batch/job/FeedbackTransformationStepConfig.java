package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultsWriter;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.FeedbackProcessor;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemReader;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by alex on 20.07.2017.
 */
@Component
public class FeedbackTransformationStepConfig {

    private static final String STEP_NAME = "feedbackTransformation";

    private final StepBuilderFactory stepBuilderFactory;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public FeedbackTransformationStepConfig(StepBuilderFactory stepBuilderFactory, MongoTemplate mongoTemplate) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.mongoTemplate = mongoTemplate;
    }

    @Bean
    public Step transformFeedback() {
        return stepBuilderFactory.get(STEP_NAME)
                .<ArdocResult, TransformedFeedback>chunk(10)
                .reader(feedbackReader())
                .processor(feedbackProcessor())
                .writer(writer())
                .build();
    }

    @Bean
    public MongoItemReader<ArdocResult> feedbackReader() {
        MongoItemReader<ArdocResult> reader = new MongoItemReader<>();
        reader.setTemplate(mongoTemplate);
        reader.setCollection(ArdocResultsWriter.COLLECTION_NAME);
        reader.setQuery("{}");
        Map<String, Sort.Direction> sort = new HashMap<String, Sort.Direction>();
        sort.put("_id", Sort.Direction.ASC);
        reader.setSort(sort);
        reader.setTargetType(ArdocResult.class);
        return reader;
    }

    @Bean
    public ItemProcessor<ArdocResult, TransformedFeedback> feedbackProcessor() {
        return new FeedbackProcessor();
    }

    @Bean
    public ItemWriter<TransformedFeedback> writer() {
        MongoItemWriter<TransformedFeedback> writer = new MongoItemWriter<>();
        writer.setTemplate(mongoTemplate);
        return writer;
    }
}
