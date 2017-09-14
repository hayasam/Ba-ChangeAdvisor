package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.source.*;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class SourceCodeService {

    private final JobLauncher jobLauncher;

    private final SourceCodeDirectoryRepository repository;

    private final SourceImportJobFactory sourceImportJobFactory;

    @Autowired
    public SourceCodeService(JobLauncher jobLauncher, SourceCodeDirectoryRepository repository, SourceImportJobFactory sourceImportJobFactory) {
        this.jobLauncher = jobLauncher;
        this.repository = repository;
        this.sourceImportJobFactory = sourceImportJobFactory;
    }

    /**
     * @param dto
     * @return
     * @deprecated Prefer using {{@link #startSourceCodeDownload(SourceCodeDirectoryDto)}}.
     */
    public SourceCodeDirectory addSourceDirectory(SourceCodeDirectoryDto dto) {
        SourceCodeImporter importer = SourceCodeImporterFactory.getImporter(dto.getPath());
        importer.setCredentials(dto.getUsername(), dto.getPassword());
        SourceCodeDirectory sourceCodeDirectory = importer.importSource();

        Optional<SourceCodeDirectory> byProjectName = repository.findByProjectName(sourceCodeDirectory.getProjectName());
        byProjectName.ifPresent(persistedDirectory -> sourceCodeDirectory.setId(persistedDirectory.getId()));
        return repository.save(sourceCodeDirectory);
    }

    /**
     * Given a dto containing a path to a directory (File system or remote) and credentials (Optional),
     * adds the directory and project to the database for later retrieval.
     *
     * @param dto Value object containing the path to a project.
     * @return a job execution instance representing the adding of a project.
     * @throws JobParametersInvalidException
     * @throws JobExecutionAlreadyRunningException
     * @throws JobRestartException
     * @throws JobInstanceAlreadyCompleteException
     */
    public JobExecution startSourceCodeDownload(SourceCodeDirectoryDto dto) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Job job = sourceImportJobFactory.job(dto);
        return jobLauncher.run(job, parametersWithCurrentTimestamp());
    }

    private JobParameters parametersWithCurrentTimestamp() {
        return new JobParametersBuilder().addDate("timestamp", new Date()).toJobParameters();
    }
}
