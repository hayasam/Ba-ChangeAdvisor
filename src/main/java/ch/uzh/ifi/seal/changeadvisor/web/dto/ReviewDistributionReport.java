package ch.uzh.ifi.seal.changeadvisor.web.dto;

import com.google.common.collect.ImmutableSet;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ReviewDistributionReport implements Iterable<ReviewCategory> {

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

    public boolean hasCategory(final String category) {
        for (ReviewCategory reviewCategory : this) {
            if (reviewCategory.getCategory().equalsIgnoreCase(category)) {
                return true;
            }
        }
        return false;
    }

    public ReviewCategory findForCategory(final String category) {
        for (ReviewCategory reviewCategory : this) {
            if (reviewCategory.getCategory().equalsIgnoreCase(category)) {
                return reviewCategory;
            }
        }
        throw new IllegalArgumentException(String.format("Found no category %s", category));
    }

    @NotNull
    @Override
    public Iterator<ReviewCategory> iterator() {
        return distribution.iterator();
    }


}
