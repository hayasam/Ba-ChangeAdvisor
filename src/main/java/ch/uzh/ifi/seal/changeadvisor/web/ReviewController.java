package ch.uzh.ifi.seal.changeadvisor.web;


import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewRepository;
import ch.uzh.ifi.seal.changeadvisor.service.ArdocService;
import ch.uzh.ifi.seal.changeadvisor.service.JobService;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewImportService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewAnalysisDto;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
public class ReviewController {

    private static final Logger logger = Logger.getLogger(ReviewController.class);

    private final ReviewImportService reviewImportService;

    private final ReviewRepository repository;

    private final ArdocService ardocService;

    private final JobHolder jobHolder;

    @Autowired
    public ReviewController(ReviewImportService reviewImportService, ReviewRepository repository, ArdocService ardocService, JobHolder jobHolder) {
        this.reviewImportService = reviewImportService;
        this.repository = repository;
        this.ardocService = ardocService;
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

    @GetMapping(path = "reviews/lastAnalyzed")
    public ArdocResult lastResultAnalyzed(@RequestParam("app") String app) {
        if (StringUtils.isEmpty(app)) {
            throw new IllegalArgumentException("Need an app name!");
        }
        return ardocService.getLastAnalyzed(app);
    }

    @GetMapping(path = "reviews/sinceLastAnalyzed")
    public List<Review> reviewsSinceLastAnalyzed(@RequestParam("app") String app) {
        if (StringUtils.isEmpty(app)) {
            throw new IllegalArgumentException("Need an app name!");
        }
        return ardocService.getReviewsSinceLastAnalyzed(app);
    }
}
