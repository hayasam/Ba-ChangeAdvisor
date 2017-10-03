package ch.uzh.ifi.seal.changeadvisor.service;


import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewRepository;
import ch.uzh.ifi.seal.changeadvisor.tfidf.*;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewCategory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewDistributionReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewsByTopLabelsDto;
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

    private final ReviewRepository repository;

    private final ArdocResultRepository ardocRepository;

    @Autowired
    public ReviewAggregationService(MongoTemplate mongoOperations, TfidfService tfidfService, ReviewRepository repository, ArdocResultRepository ardocRepository) {
        this.mongoOperations = mongoOperations;
        this.tfidfService = tfidfService;
        this.repository = repository;
        this.ardocRepository = ardocRepository;
    }

    public ReviewDistributionReport groupByCategories(final String appName) {
        TypedAggregation<ArdocResult> categoryAggregation = Aggregation.newAggregation(ArdocResult.class,
                Aggregation.match(Criteria.where("appName").is(appName)),
                Aggregation.group("category").first("category").as("category") // set group by field and save it as 'category' in resulting object.
                        .push("$$ROOT").as("reviews") // push entire document to field 'reviews' in ReviewCategory.
        );

        AggregationResults<ReviewCategory> groupResults =
                mongoOperations.aggregate(categoryAggregation, ArdocResult.class, ReviewCategory.class);

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
    public List<LabelWithReviews> reviewsByTopNLabels(ReviewsByTopLabelsDto dto) {
        List<Label> labels = topNLabels(dto);

        logger.info(String.format("Fetching reviews for top %d labels: %s", dto.getLimit(), labels));
        List<LabelWithReviews> labelWithReviews = new ArrayList<>(labels.size());
        for (Label label : labels) {
            List<ArdocResult> ardocResults =
                    ardocRepository.findByAppNameAndCategoryAndSentenceContainingIgnoreCase(dto.getApp(), dto.getCategory(), label.getLabel());
            List<Review> reviews = ardocResults.stream().map(ArdocResult::getReview).collect(Collectors.toList());
            labelWithReviews.add(new LabelWithReviews(label.getLabel(), reviews));
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
        ReviewDistributionReport reviewsByCategory = groupByCategories(dto.getApp());
        final String category = dto.getCategory();
        Assert.isTrue(reviewsByCategory.hasCategory(category), String.format("Unknown category %s", category));

        List<Label> tokensWithScore = getNgramTokensWithScore(reviewsByCategory, category, dto.getNgrams());

        Collections.sort(tokensWithScore, Collections.reverseOrder());
        logger.info(tokensWithScore.size());
        final int limit = dto.getLimit();
        if (!dto.hasLimit() || limit >= tokensWithScore.size()) {
            return tokensWithScore;
        }
        return tokensWithScore.subList(0, limit);
    }

    private List<Label> getNgramTokensWithScore(ReviewDistributionReport reviewsByCategory, final String category, final int ngramSize) {
        Map<String, Document> categoryDocumentMap = mapReviewsToDocuments(reviewsByCategory, ngramSize);

        Corpus corpus = new Corpus(categoryDocumentMap.values());
        Document document = categoryDocumentMap.get(category);
        List<AbstractNGram> uniqueTokens = document.uniqueTokens();

        return tfidfService.computeTfidfScoreForTokens(uniqueTokens, document, corpus);
    }

    private Map<String, Document> mapReviewsToDocuments(ReviewDistributionReport reviewDistribution, final int ngramSize) {
        Map<String, Document> categoryDocumentMap = new HashMap<>();
        for (ReviewCategory reviewCategory : reviewDistribution) {
            categoryDocumentMap.put(reviewCategory.getCategory(), reviewCategory.asDocument(ngramSize));
        }
        return categoryDocumentMap;
    }
}
