package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.source.SourceImportJobFactory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SourceCodeService {

    private final JobService jobService;

    private final SourceImportJobFactory sourceImportJobFactory;

    @Autowired
    public SourceCodeService(JobService jobService, SourceImportJobFactory sourceImportJobFactory) {
        this.jobService = jobService;
        this.sourceImportJobFactory = sourceImportJobFactory;
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
        return jobService.run(job);
    }
}
