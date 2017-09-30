package ch.uzh.ifi.seal.changeadvisor.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ReviewsByTopLabelsDto {

    private final String app;

    private final String category;

    private final int limit;

    private final int ngrams;

    public ReviewsByTopLabelsDto(@JsonProperty(value = "app", required = true) String app,
                                 @JsonProperty(value = "category", required = true) String category,
                                 @JsonProperty(value = "limit") int limit,
                                 @JsonProperty(value = "ngrams") int ngrams) {
        this.app = app;
        this.category = category;
        this.limit = limit < 1 ? -1 : limit;
        this.ngrams = ngrams < 1 ? 1 : ngrams;
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

    public boolean hasLimit() {
        return limit > 0;
    }

    public int getNgrams() {
        return ngrams;
    }
}
