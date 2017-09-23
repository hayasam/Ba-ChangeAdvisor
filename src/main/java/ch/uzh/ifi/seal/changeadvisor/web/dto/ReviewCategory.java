package ch.uzh.ifi.seal.changeadvisor.web.dto;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;

public class ReviewCategory {

    private final Set<ArdocResult> reviews;

    private final String category;

    public ReviewCategory(Collection<ArdocResult> reviews, String category) {
        this.reviews = ImmutableSet.copyOf(reviews);
        this.category = category;
    }

    @JsonIgnore
    public Set<ArdocResult> getReviews() {
        return reviews;
    }

    public String getCategory() {
        return category;
    }

    public int size() {
        return reviews.size();
    }

    public int getReviewSize() {
        return size();
    }
}
