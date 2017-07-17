package ch.uzh.ifi.seal.changeadvisor.batch.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Overall ChangeAdvisor Job configuration.
 * Created by alex on 15.07.2017.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private static final String JOB_NAME = "changeAdvisor";

    private final JobBuilderFactory jobBuilderFactory;

    private final ExtractBagOfWordsStepConfig bagOfWordsStepConfig;

    private final ArdocStepConfig ardocStepConfig;

    @Autowired
    public BatchConfig(JobBuilderFactory jobBuilderFactory, ExtractBagOfWordsStepConfig bagOfWordsStepConfig, ArdocStepConfig ardocStepConfig) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.bagOfWordsStepConfig = bagOfWordsStepConfig;
        this.ardocStepConfig = ardocStepConfig;
    }

    @Bean
    public Job changeAdvisor(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get(JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(bagOfWordsStepConfig.extractBagOfWords())
                .next(ardocStepConfig.ardocAnalysis())
                .end()
                .build();
    }

}
