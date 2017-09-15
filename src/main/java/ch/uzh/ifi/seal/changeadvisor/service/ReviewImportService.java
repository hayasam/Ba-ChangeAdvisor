package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewImportJobFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReviewImportService {

    private final ReviewImportJobFactory reviewImportJobFactory;

    private final JobService jobService;

    public ReviewImportService(ReviewImportJobFactory reviewImportJobFactory, JobService jobService) {
        this.reviewImportJobFactory = reviewImportJobFactory;
        this.jobService = jobService;
    }

    public JobExecution reviewImport(Map<String, Object> params) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Job reviewImport = reviewImportJobFactory.job(params);
        return jobService.run(reviewImport);
    }
}
