package ch.uzh.ifi.seal.changeadvisor.source;

import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SourceImportJobFactory {

    private static final String SOURCE_IMPORT = "sourceImport";

    private static final String STEP_NAME = "sourceImportStep";

    private final StepBuilderFactory stepBuilderFactory;

    private final JobBuilderFactory jobBuilderFactory;

    private final SourceCodeDirectoryRepository repository;

    @Autowired
    public SourceImportJobFactory(StepBuilderFactory stepBuilderFactory, JobBuilderFactory jobBuilderFactory, SourceCodeDirectoryRepository repository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = jobBuilderFactory;
        this.repository = repository;
    }

    public Job job(SourceCodeDirectoryDto dto) {
        return jobBuilderFactory.get(SOURCE_IMPORT)
                .incrementer(new RunIdIncrementer())
                .flow(sourceImport(dto))
                .end()
                .build();
    }

    private Step sourceImport(SourceCodeDirectoryDto dto) {
        return stepBuilderFactory.get(STEP_NAME)
                .allowStartIfComplete(true)
                .tasklet(new SourceImportTasklet(dto, repository))
                .build();
    }

}
