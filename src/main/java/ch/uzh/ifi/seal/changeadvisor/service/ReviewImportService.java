package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewImportJobFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ReviewImportService {

    private final ReviewImportJobFactory reviewImportJobFactory;

    private final JobLauncher jobLauncher;

    public ReviewImportService(ReviewImportJobFactory reviewImportJobFactory, JobLauncher jobLauncher) {
        this.reviewImportJobFactory = reviewImportJobFactory;
        this.jobLauncher = jobLauncher;
    }

    public JobExecution reviewImport(Map<String, Object> params) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        Job reviewImport = reviewImportJobFactory.job(params);
        return jobLauncher.run(reviewImport, new JobParameters());
    }
}
