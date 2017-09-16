package ch.uzh.ifi.seal.changeadvisor.web;


import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewRepository;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewImportService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ExecutionReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewAnalysisDto;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ReviewController {

    private static final Logger logger = Logger.getLogger(ReviewController.class);

    private final ReviewImportService reviewImportService;

    private final ReviewRepository repository;

    private final SessionUtil sessionUtil;

    @Autowired
    public ReviewController(ReviewImportService reviewImportService, ReviewRepository repository, SessionUtil sessionUtil) {
        this.reviewImportService = reviewImportService;
        this.repository = repository;
        this.sessionUtil = sessionUtil;
    }

    @PostMapping(path = "reviews")
    public long reviewImport(@RequestBody Map<String, Object> params) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {
        Assert.notEmpty(params, "Empty or null parameters. Need at least list of apps.");
        Assert.isTrue(params.containsKey("apps"), "Request has to contain list of apps.");
        logger.info(String.format("Creating review import job and starting process with parameters %s.", params));
        JobExecution jobExecution = reviewImportService.reviewImport(params);
        return jobExecution.getJobId();
    }

    @GetMapping(path = "reviews")
    public Collection<Review> reviews() {
        List<Review> all = repository.findAll();
        return all;
    }

    @PostMapping(path = "reviews/analyze")
    public long reviewAnalysis(@RequestBody @Valid ReviewAnalysisDto dto) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        logger.info(String.format("Starting analysis job for app %s!", dto.getApp()));
        JobExecution jobExecution = reviewImportService.reviewAnalysis(dto);
        sessionUtil.addJob(jobExecution);
        return jobExecution.getJobId();
    }

    @GetMapping(path = "reviews/analyze/status/{jobId}")
    @ResponseBody
    public Collection<ExecutionReport> reviewAnalysis(@PathVariable(name = "jobId") Long jobId) {
        if (sessionUtil.hasJob(jobId)) {
            JobExecution job = sessionUtil.getJob(jobId);
            Collection<StepExecution> stepExecutions = job.getStepExecutions();
            return stepExecutions
                    .stream()
                    .map(ExecutionReport::of)
                    .collect(Collectors.toList());
        }
        throw new IllegalArgumentException(String.format("No job found for job id: %d", jobId));
    }
}
