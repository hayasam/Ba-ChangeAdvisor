package ch.uzh.ifi.seal.changeadvisor.service;


import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.LabelRepository;
import ch.uzh.ifi.seal.changeadvisor.tfidf.AbstractNGram;
import ch.uzh.ifi.seal.changeadvisor.tfidf.Corpus;
import ch.uzh.ifi.seal.changeadvisor.tfidf.Document;
import ch.uzh.ifi.seal.changeadvisor.tfidf.TfidfService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.*;
import edu.emory.mathcs.backport.java.util.Collections;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReviewAggregationService {

    private static final Logger logger = Logger.getLogger(ReviewAggregationService.class);

    private final MongoTemplate mongoOperations;

    private final TfidfService tfidfService;

    private final TransformedFeedbackRepository transformedFeedbackRepository;

    private final LabelRepository labelRepository;

    @Autowired
    public ReviewAggregationService(MongoTemplate mongoOperations, TfidfService tfidfService,
                                    TransformedFeedbackRepository transformedFeedbackRepository, LabelRepository labelRepository) {
        this.mongoOperations = mongoOperations;
        this.tfidfService = tfidfService;
        this.transformedFeedbackRepository = transformedFeedbackRepository;
        this.labelRepository = labelRepository;
    }

    /**
     * Generates a category distribution report.
     * Groups reviews by ardoc category.
     *
     * @param appName application for which we want to generate a report.
     * @return distribution report.
     * @see ReviewDistributionReport
     * @see ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult#category
     */
    public ReviewDistributionReport groupByCategories(final String appName) {
        TypedAggregation<TransformedFeedback> categoryAggregation = Aggregation.newAggregation(TransformedFeedback.class,
                Aggregation.match(Criteria.where("ardocResult.appName").is(appName)),
                Aggregation.group("ardocResult.category").first("ardocResult.category").as("category") // set group by field and save it as 'category' in resulting object.
                        .push("$$ROOT").as("reviews") // push entire document to field 'reviews' in ReviewCategory.
        );

        AggregationResults<ReviewCategory> groupResults =
                mongoOperations.aggregate(categoryAggregation, TransformedFeedback.class, ReviewCategory.class);

        return new ReviewDistributionReport(groupResults.getMappedResults());
    }

    /**
     * Retrieves the reviews based on the top N labels.
     * Fetches all reviews which contain these top labels.
     *
     * @param dto object representing the parameters we use to compute the top N labels
     *            (e.g. how many labels and for which app)
     * @return reviews for the top N labels.
     */
    public List<LabelWithReviews> reviewsByTopNLabelsByCategory(ReviewsByTopLabelsDto dto) {
        List<Label> labels = labelRepository.findByAppNameAndCategoryAndNgramSizeOrderByScoreDesc(dto.getApp(), dto.getCategory(), dto.getNgrams());
        final int limit = dto.getLimit();
        if (dto.hasLimit() && limit < labels.size()) {
            labels = labels.subList(0, dto.getLimit());
        }

        logger.info(String.format("Fetching reviews for top %d labels: %s", dto.getLimit(), labels));
        List<LabelWithReviews> labelWithReviews = new ArrayList<>(labels.size());
        for (Label label : labels) {
            List<TransformedFeedback> feedback = transformedFeedbackRepository.findByArdocResultAppNameAndArdocResultCategoryAndTransformedSentenceContainingIgnoreCase(dto.getApp(), dto.getCategory(), label.getLabel());
            // Two ardoc results could be mapped to the same review, so in this step we remove duplicate reviews.
            List<Review> reviews = feedback.stream().map(TransformedFeedback::getReview).distinct().collect(Collectors.toList());
            labelWithReviews.add(new LabelWithReviews(label, reviews));
        }

        return labelWithReviews;
    }

    /**
     * Retrieves the reviews based on the top N labels.
     * Fetches all reviews which contain these top labels.
     *
     * @param dto object representing the parameters we use to compute the top N labels
     *            (e.g. how many labels and for which app)
     * @return reviews for the top N labels.
     */
    public List<LabelWithReviews> reviewsByTopNLabels(ReviewsByTopLabelsDto dto) {
        List<Label> labels = labelRepository.findByAppNameAndNgramSizeOrderByScoreDesc(dto.getApp(), dto.getNgrams());
        final int limit = dto.getLimit();
        if (dto.hasLimit() && limit < labels.size()) {
            labels = labels.subList(0, dto.getLimit());
        }

        logger.info(String.format("Fetching reviews for top %d labels: %s", dto.getLimit(), labels));
        List<LabelWithReviews> labelWithReviews = new ArrayList<>(labels.size());
        for (Label label : labels) {
            List<TransformedFeedback> feedback = transformedFeedbackRepository.findByArdocResultAppNameAndTransformedSentenceContainingIgnoreCase(dto.getApp(), label.getLabel());
            // Two ardoc results could be mapped to the same review, so in this step we remove duplicate reviews.
            List<ReviewWithCategory> reviews = feedback.stream().map(f -> new ReviewWithCategory(f.getReview(), f.getCategory())).distinct().collect(Collectors.toList());
            java.util.Collections.sort(reviews);
            labelWithReviews.add(new LabelWithReviews(label, reviews));
        }

        return labelWithReviews;
    }

    /**
     * Retrieves the top N labels for a set of reviews.
     * A label is an Ngram of tokens that are representative for a group of reviews.
     *
     * @param dto object representing the parameters we use to compute the top N labels
     *            (e.g. how many labels and for which app)
     * @return list of labels with their tfidf score.
     * @see ReviewsByTopLabelsDto
     */
    public List<Label> topNLabels(ReviewsByTopLabelsDto dto) {
        // READER
        ReviewDistributionReport reviewsByCategory = groupByCategories(dto.getApp());
        final String category = dto.getCategory();
        Assert.isTrue(reviewsByCategory.hasCategory(category), String.format("Unknown category %s", category));

        // PROCESSOR
        List<Label> tokensWithScore = getNgramTokensWithScore(reviewsByCategory, dto);

        Collections.sort(tokensWithScore, Collections.reverseOrder());

        // WRITER
        final int limit = dto.getLimit();
        if (!dto.hasLimit() || limit >= tokensWithScore.size()) {
            return tokensWithScore;
        }
        return tokensWithScore.subList(0, limit);
    }

    private List<Label> getNgramTokensWithScore(ReviewDistributionReport reviewsByCategory, ReviewsByTopLabelsDto dto) {
        Map<String, Document> categoryDocumentMap = mapReviewsToDocuments(reviewsByCategory, dto.getNgrams());

        Corpus corpus = new Corpus(categoryDocumentMap.values());
        Document document = categoryDocumentMap.get(dto.getCategory());
        List<AbstractNGram> uniqueTokens = document.uniqueTokens();

        return tfidfService.computeTfidfScoreForTokens(dto.getApp(), dto.getCategory(), uniqueTokens, document, corpus);
    }

    private Map<String, Document> mapReviewsToDocuments(ReviewDistributionReport reviewDistribution, final int ngramSize) {
        Map<String, Document> categoryDocumentMap = new HashMap<>();
        for (ReviewCategory reviewCategory : reviewDistribution) {
            categoryDocumentMap.put(reviewCategory.getCategory(), reviewCategory.asDocument(ngramSize));
        }
        return categoryDocumentMap;
    }
}
