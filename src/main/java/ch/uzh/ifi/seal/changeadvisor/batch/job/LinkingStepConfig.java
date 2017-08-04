package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicClusteringResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.ChangeAdvisorLinker;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingProcessor;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingStepReader;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinkingStepConfig {

    private static final String STEP_NAME = "linking";

    private final StepBuilderFactory stepBuilderFactory;

    private final LinkingStepReader linkingStepReader;

    @Autowired
    public LinkingStepConfig(StepBuilderFactory stepBuilderFactory, LinkingStepReader linkingStepReader) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.linkingStepReader = linkingStepReader;
    }

    @Bean
    public Step transformFeedback() {

        return stepBuilderFactory.get(STEP_NAME)
                .<TopicClusteringResult, List<LinkingResult>>chunk(1)
                .reader(linkingStepReader)
                .processor(processor())
                .build();
    }

    @Bean
    public LinkingProcessor processor() {
        return new LinkingProcessor(new ChangeAdvisorLinker());
    }
}
