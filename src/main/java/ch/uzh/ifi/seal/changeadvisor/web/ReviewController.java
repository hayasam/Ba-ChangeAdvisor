package ch.uzh.ifi.seal.changeadvisor.web;


import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewRepository;
import ch.uzh.ifi.seal.changeadvisor.service.JobService;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewImportService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewAnalysisDto;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {

    private static final Logger logger = Logger.getLogger(ReviewController.class);

    private final ReviewImportService reviewImportService;

    private final ReviewRepository repository;

    private final JobHolder jobHolder;

    @Autowired
    public ReviewController(ReviewImportService reviewImportService, ReviewRepository repository, JobHolder jobHolder) {
        this.reviewImportService = reviewImportService;
        this.repository = repository;
        this.jobHolder = jobHolder;
    }

    @PostMapping(path = "reviews")
    public long reviewImport(@RequestBody Map<String, Object> params) throws JobService.FailedToRunJobException {
        Assert.notEmpty(params, "Empty or null parameters. Need at least list of apps.");
        Assert.isTrue(params.containsKey("apps"), "Request has to contain list of apps.");
        logger.info(String.format("Creating review import job and starting process with parameters %s.", params));
        JobExecution jobExecution = reviewImportService.reviewImport(params);
        jobHolder.addJob(jobExecution);
        return jobExecution.getJobId();
    }

    @GetMapping(path = "reviews")
    public Collection<Review> reviews() {
        List<Review> all = repository.findAll();
        return all;
    }

    @PostMapping(path = "reviews/analyze")
    public long reviewAnalysis(@RequestBody @Valid ReviewAnalysisDto dto) throws JobService.FailedToRunJobException {
        logger.info(String.format("Starting analysis job for app %s!", dto.getApp()));
        JobExecution jobExecution = reviewImportService.reviewAnalysis(dto);
        jobHolder.addJob(jobExecution);
        return jobExecution.getJobId();
    }
}
