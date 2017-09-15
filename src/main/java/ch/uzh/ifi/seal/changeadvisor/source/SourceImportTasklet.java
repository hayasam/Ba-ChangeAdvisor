package ch.uzh.ifi.seal.changeadvisor.source;

import ch.uzh.ifi.seal.changeadvisor.source.importer.SourceCodeImporter;
import ch.uzh.ifi.seal.changeadvisor.source.importer.SourceCodeImporterFactory;
import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectoryRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Optional;

public class SourceImportTasklet implements Tasklet {

    private SourceCodeDirectoryDto dto;

    private SourceCodeDirectoryRepository repository;

    public SourceImportTasklet(SourceCodeDirectoryDto dto, SourceCodeDirectoryRepository repository) {
        this.dto = dto;
        this.repository = repository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        SourceCodeImporter importer = SourceCodeImporterFactory.getImporter(dto);
        SourceCodeDirectory sourceCodeDirectory = importer.importSource();
        SourceCodeDirectory persistedDirectory = saveOrUpdateDirectory(sourceCodeDirectory);

        writeIntoExecutionContext(chunkContext, persistedDirectory);
        return RepeatStatus.FINISHED;
    }

    private SourceCodeDirectory saveOrUpdateDirectory(SourceCodeDirectory directory) {
        Optional<SourceCodeDirectory> byProjectName = repository.findByProjectName(directory.getProjectName());
        byProjectName.ifPresent(persistedDirectory -> directory.setId(persistedDirectory.getId()));
        return repository.save(directory);
    }

    private <T> void writeIntoExecutionContext(ChunkContext context, T directory) {
        context.getStepContext().getStepExecution().getExecutionContext().put("directory", directory);
    }
}
