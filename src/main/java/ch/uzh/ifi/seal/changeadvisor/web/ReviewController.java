package ch.uzh.ifi.seal.changeadvisor.web;


import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewRepository;
import ch.uzh.ifi.seal.changeadvisor.service.ArdocService;
import ch.uzh.ifi.seal.changeadvisor.service.FailedToRunJobException;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewAggregationService;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewImportService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewAnalysisDto;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewCategory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewDistributionReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewsByTopLabelsDto;
import ch.uzh.ifi.seal.changeadvisor.web.util.*;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ReviewController {

    private static final Logger logger = Logger.getLogger(ReviewController.class);

    private final ReviewImportService reviewImportService;

    private final ReviewRepository repository;

    private final ArdocService ardocService;

    private final ReviewAggregationService aggregationService;

    @Autowired
    public ReviewController(ReviewImportService reviewImportService, ReviewRepository repository, ArdocService ardocService, ReviewAggregationService service) {
        this.reviewImportService = reviewImportService;
        this.repository = repository;
        this.ardocService = ardocService;
        this.aggregationService = service;
    }

    @PostMapping(path = "reviews")
    public long reviewImport(@RequestBody Map<String, Object> params) throws FailedToRunJobException {
        Assert.notEmpty(params, "Empty or null parameters. Need at least list of apps.");
        Assert.isTrue(params.containsKey("apps"), "Request has to contain list of apps.");
        logger.info(String.format("Creating review import job and starting process with parameters %s.", params));
        JobExecution jobExecution = reviewImportService.reviewImport(params);
        return jobExecution.getJobId();
    }

    @GetMapping(path = "reviews")
    public Collection<Review> reviews() {
        return repository.findAll();
    }

    @PostMapping(path = "reviews/analyze")
    public long reviewAnalysis(@RequestBody @Valid ReviewAnalysisDto dto) throws FailedToRunJobException {
        logger.info(String.format("Starting analysis job for app %s!", dto.getApp()));
        JobExecution jobExecution = reviewImportService.reviewAnalysis(dto);
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

    @PostMapping(path = "reviews/processing")
    public long reviewsProcessing(@RequestBody @Valid ReviewAnalysisDto dto) throws FailedToRunJobException {
        logger.info(String.format("Starting reviews processing job for app %s!", dto.getApp()));
        JobExecution jobExecution = reviewImportService.reviewProcessing(dto);
        return jobExecution.getJobId();
    }

    @PostMapping(path = "reviews/clustering")
    public long reviewsClustering(@RequestBody @Valid ReviewAnalysisDto dto) throws FailedToRunJobException {
        logger.info(String.format("Starting reviews clustering job for app %s!", dto.getApp()));
        JobExecution jobExecution = reviewImportService.reviewClustering(dto);
        return jobExecution.getJobId();
    }

    @PostMapping(path = "reviews/distribution")
    public ReviewDistributionReport report(@RequestBody @Valid ReviewAnalysisDto dto) {
        return aggregationService.groupByCategories(dto.getApp());
    }

    @PostMapping(path = "reviews/top")
    public List<TfidfToken> reviewsByTopNLabels(@RequestBody ReviewsByTopLabelsDto dto) {
        ReviewDistributionReport reviewsByCategory = aggregationService.groupByCategories(dto.getApp());
        final String category = dto.getCategory();
        Assert.isTrue(reviewsByCategory.hasCategory(category), String.format("Unknown category %s", category));

        List<TfidfToken> tokensWithScore = getNgramTokensWithScore(reviewsByCategory, category, dto.getNgrams());

        Collections.sort(tokensWithScore, Collections.reverseOrder());
        logger.info(tokensWithScore.size());
        final int limit = dto.getLimit();
        if (!dto.hasLimit() || limit >= tokensWithScore.size()) {
            return tokensWithScore;
        }
        return tokensWithScore.subList(0, limit);
    }

    private List<TfidfToken> getNgramTokensWithScore(ReviewDistributionReport reviewsByCategory, final String category, final int ngramSize) {
        Map<String, Document> categoryDocumentMap = new HashMap<>();
        for (ReviewCategory reviewCategory : reviewsByCategory) {
            categoryDocumentMap.put(reviewCategory.getCategory(), reviewCategory.asDocument(ngramSize));
        }

        Document document = categoryDocumentMap.get(category);
        List<? extends AbstractNGram> uniqueTokens = document.uniqueTokens();

        TFiDF tFiDF = new TFiDF();
        Corpus corpus = new Corpus(categoryDocumentMap.values());
        return uniqueTokens
                .stream()
                .map(token -> new TfidfToken(token, tFiDF.computeTfidf(token, document, corpus)))
                .collect(Collectors.toList());
    }
}
