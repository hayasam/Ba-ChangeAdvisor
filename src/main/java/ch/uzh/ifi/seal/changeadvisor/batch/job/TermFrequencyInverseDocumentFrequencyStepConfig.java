package ch.uzh.ifi.seal.changeadvisor.batch.job;

import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TermFrequencyInverseDocumentFrequencyStepConfig {

    private final StepBuilderFactory stepBuilderFactory;

    private static final String STEP_NAME = "feedbackTransformation";

    @Autowired
    public TermFrequencyInverseDocumentFrequencyStepConfig(StepBuilderFactory stepBuilderFactory) {
        this.stepBuilderFactory = stepBuilderFactory;
    }

}
