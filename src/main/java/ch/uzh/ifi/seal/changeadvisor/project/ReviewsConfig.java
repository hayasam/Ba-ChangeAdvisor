package ch.uzh.ifi.seal.changeadvisor.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings("unused")
public class ReviewsConfig implements Serializable {

    private final Date lastReviewImport;

    @JsonCreator
    public ReviewsConfig(@JsonProperty("lastReviewImport") Date lastReviewImport) {
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
