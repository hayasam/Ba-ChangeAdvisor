package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import com.google.common.collect.ImmutableList;
import org.springframework.util.Assert;

import java.util.List;

public class LabelWithReviews {

    private final String label;

    private final List<Review> reviews;

    public LabelWithReviews(String label, List<Review> reviews) {
        Assert.notNull(reviews, "Reviews cannot be null! In case of no results make sure to pass an empty list.");
        this.label = label;
        this.reviews = ImmutableList.copyOf(reviews);
    }

    public String getLabel() {
        return label;
    }

    public int reviewCount() {
        return reviews.size();
    }

    public List<Review> getReviews() {
        return reviews;
    }

    @Override
    public String toString() {
        return "LabelWithReviews{" +
                "label='" + label + '\'' +
                ", reviews=" + reviews +
                '}';
    }
}
