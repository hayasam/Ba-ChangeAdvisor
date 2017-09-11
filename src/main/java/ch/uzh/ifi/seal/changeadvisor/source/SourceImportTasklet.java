package ch.uzh.ifi.seal.changeadvisor.source;

import ch.uzh.ifi.seal.changeadvisor.web.SourceCodeDirectoryDto;
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
        SourceCodeImporter importer = SourceCodeImporterFactory.getImporter(dto.getPath());
        importer.setCredentials(dto.getUsername(), dto.getPassword());
        SourceCodeDirectory sourceCodeDirectory = importer.importSource();

        Optional<SourceCodeDirectory> byProjectName = repository.findByProjectName(sourceCodeDirectory.getProjectName());
        byProjectName.ifPresent(persistedDirectory -> sourceCodeDirectory.setId(persistedDirectory.getId()));
        repository.save(sourceCodeDirectory);

        return null;
    }
}
