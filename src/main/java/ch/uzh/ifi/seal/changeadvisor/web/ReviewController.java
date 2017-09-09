package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.service.ReviewImportService;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

@RestController
public class ReviewController {

    private static final Logger logger = Logger.getLogger(ReviewController.class);

    private final ReviewImportService reviewImportService;

    @Autowired
    public ReviewController(ReviewImportService reviewImportService) {
        this.reviewImportService = reviewImportService;
    }

    @PostMapping(path = "reviews")
    public long reviewImport(@RequestBody Map<String, Object> params) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException, IOException {
        Assert.notEmpty(params, "Empty or null parameters. Need at least list of apps.");
        Assert.isTrue(params.containsKey("apps"), "Request has to contain list of apps.");
        logger.info(String.format("Creating review import job and starting process with paramaters %s.", params));
        JobExecution jobExecution = reviewImportService.reviewImport(params);
        return jobExecution.getJobId();
    }
}
