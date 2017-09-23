package ch.uzh.ifi.seal.changeadvisor.web.dto;

import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.Set;

public class ReviewDistributionReport {

    private Set<ReviewCategory> distribution;

    public ReviewDistributionReport(Collection<ReviewCategory> distribution) {
        this.distribution = ImmutableSet.copyOf(distribution);
    }

    public int getTotalReviews() {
        return distribution.stream().mapToInt(ReviewCategory::size).sum();
    }

    public Set<ReviewCategory> getDistribution() {
        return distribution;
    }
}
