package ch.uzh.ifi.seal.changeadvisor.project;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("unused")
public class ReviewsConfig implements Serializable {

    private final Date lastReviewImport;

    public ReviewsConfig(Date lastReviewImport) {
        this.lastReviewImport = lastReviewImport;
    }

    public Date getLastReviewImport() {
        return lastReviewImport;
    }

    @Override
    public String toString() {
        return "ReviewsConfig{" +
                "lastReviewImport=" + lastReviewImport +
                '}';
    }
}
