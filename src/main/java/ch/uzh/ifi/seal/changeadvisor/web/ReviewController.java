package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewImportJobFactory;
import org.apache.log4j.Logger;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
public class ReviewController {

    private static final Logger logger = Logger.getLogger(ReviewController.class);

    private final SessionUtil sessionUtil;

    private final ReviewImportJobFactory reviewImportJobFactory;

    private final JobLauncher jobLauncher;

    @Autowired
    public ReviewController(SessionUtil sessionUtil, ReviewImportJobFactory jobFactory, JobLauncher jobLauncher) {
        this.sessionUtil = sessionUtil;
        this.reviewImportJobFactory = jobFactory;
        this.jobLauncher = jobLauncher;
    }

    @PostMapping(path = "reviews")
    public long reviewImport(@RequestBody Map<String, Object> params) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {
        Job reviewImport = reviewImportJobFactory.job(params);

        JobExecution jobExecution = jobLauncher.run(reviewImport, new JobParameters());
        sessionUtil.putJob(jobExecution);
        return jobExecution.getJobId();
    }

    @GetMapping(path = "reviews/status/{id}")
    public BatchStatus status(@PathVariable("id") Long jobId) {
        return sessionUtil.getStatus(jobId);
    }
}
