package ch.uzh.ifi.seal.changeadvisor.source;

import ch.uzh.ifi.seal.changeadvisor.batch.job.SourceComponentsTransformationStepConfig;
import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectoryRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.ExecutionContextPromotionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SourceImportJobFactory {

    private static final String SOURCE_IMPORT = "sourceImport";

    private static final String STEP_NAME = "sourceImportStep";

    private final StepBuilderFactory stepBuilderFactory;

    private final JobBuilderFactory jobBuilderFactory;

    private final SourceCodeDirectoryRepository repository;

    private final SourceComponentsTransformationStepConfig sourceComponentsTransformationStepConfig;

    @Autowired
    public SourceImportJobFactory(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory, SourceCodeDirectoryRepository repository, SourceComponentsTransformationStepConfig sourceComponentsTransformationStepConfig) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.repository = repository;
        this.sourceComponentsTransformationStepConfig = sourceComponentsTransformationStepConfig;
    }

    public Job importAndProcessingJob(SourceCodeDirectoryDto dto) {
        return jobBuilderFactory.get(SOURCE_IMPORT)
                .incrementer(new RunIdIncrementer())
                .flow(sourceImport(dto))
                .next(sourceProcessing())
                .end()
                .build();
    }

    public Job processingJob(final String appName) {
        return jobBuilderFactory.get(SOURCE_IMPORT)
                .incrementer(new RunIdIncrementer())
                .flow(sourceProcessing(appName))
                .end()
                .build();
    }

    private Step sourceImport(SourceCodeDirectoryDto dto) {
        SourceImportTasklet importTasklet = new SourceImportTasklet(dto, repository);
        return stepBuilderFactory.get(STEP_NAME)
                .allowStartIfComplete(true)
                .tasklet(importTasklet)
                .listener(executionContextPromotionListener())
                .build();
    }

    private Step sourceProcessing(final String appName) {
        Optional<SourceCodeDirectory> projectDirectory = repository.findByProjectName(appName);
        if (projectDirectory.isPresent()) {
            return sourceComponentsTransformationStepConfig.extractBagOfWords(projectDirectory.get().getPath());
        }
        throw new IllegalArgumentException(String.format("No project imported for app name %s", appName));
    }

    private Step sourceProcessing() {
        return sourceComponentsTransformationStepConfig.extractBagOfWordsDeferredPath();
    }

    private ExecutionContextPromotionListener executionContextPromotionListener() {
        ExecutionContextPromotionListener listener = new ExecutionContextPromotionListener();
        listener.setKeys(new String[]{"directory"});
        return listener;
    }
}
