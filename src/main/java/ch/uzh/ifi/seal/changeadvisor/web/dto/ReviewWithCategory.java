package ch.uzh.ifi.seal.changeadvisor.web.dto;

import ch.uzh.ifi.seal.changeadvisor.batch.job.HasReview;
import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import org.jetbrains.annotations.NotNull;

import java.util.Date;

public class ReviewWithCategory implements Comparable<ReviewWithCategory>, HasReview {

    private final String reviewId;

    private final Date reviewDate;

    private final String review;

    private final int numberOfStars;

    private final String category;

    public ReviewWithCategory(Review review, String category) {
        this.reviewId = review.getId();
        this.reviewDate = review.getReviewDate();
        this.review = review.getReviewText();
        this.numberOfStars = review.getNumberOfStars();
        this.category = category;
    }

    @Override
    public String getReviewText() {
        return review;
    }

    public String getCategory() {
        return category;
    }

    public String getReviewId() {
        return reviewId;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public int getNumberOfStars() {
        return numberOfStars;
    }

    @Override
    public String toString() {
        return "ReviewWithCategory{" +
                "review=" + review +
                ", category='" + category + '\'' +
                '}';
    }

    @Override
    public int compareTo(@NotNull ReviewWithCategory o) {
        return review.compareTo(o.review);
    }
}
