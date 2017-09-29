package ch.uzh.ifi.seal.changeadvisor.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReviewsByTopLabelsDto {

    private final String app;

    private final String category;

    private final int limit;

    private final int ngrams;

    public ReviewsByTopLabelsDto(@JsonProperty(value = "app", required = true) String app,
                                 @JsonProperty(value = "category", required = true) String category,
                                 @JsonProperty(value = "limit", defaultValue = "10") int limit,
                                 @JsonProperty(value = "ngrams", defaultValue = "1") int ngrams) {
        this.app = app;
        this.category = category;
        this.limit = limit;
        this.ngrams = ngrams;
    }

    public String getApp() {
        return app;
    }

    public String getCategory() {
        return category;
    }

    public int getLimit() {
        return limit;
    }

    public int getNgrams() {
        return ngrams;
    }
}
