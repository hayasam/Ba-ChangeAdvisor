package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.ReviewRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class ArdocService {

    private static final Logger logger = Logger.getLogger(ArdocService.class);

    private final ArdocResultRepository repository;

    private final ReviewRepository reviewRepository;

    public ArdocService(ArdocResultRepository repository, ReviewRepository reviewRepository) {
        this.repository = repository;
        this.reviewRepository = reviewRepository;
    }

    public Collection<ArdocResult> getResults(final String app) {
        List<ArdocResult> byReviewAppName = repository.findByAppName(app);
        return byReviewAppName;
    }

    public List<Review> getReviewsSinceLastAnalyzed(final String app) {
        ArdocResult lastAnalyzed = getLastAnalyzed(app);
        List<Review> reviewsSinceLastAnalyzed = reviewRepository
                .findByAppNameAndReviewDateGreaterThanEqualOrderByReviewDateDesc(app, lastAnalyzed.getReviewDate());
        logger.info(
                String.format("Found %d reviews to analyze since last ardoc run. Review Date: %s.\tArdoc timestamp: %s.",
                        reviewsSinceLastAnalyzed.size(), lastAnalyzed.getReviewDate(), lastAnalyzed.getTimestamp()));
        return reviewsSinceLastAnalyzed;
    }

    public ArdocResult getLastAnalyzed(final String app) {
        return repository.findFirstByAppNameOrderByReview_ReviewDateDesc(app)
                .orElseThrow(() ->
                        new IllegalStateException(String.format("No reviews analyzed for app %s", app)));
    }
}
