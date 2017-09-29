package ch.uzh.ifi.seal.changeadvisor.web.dto;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.web.util.Document;
import ch.uzh.ifi.seal.changeadvisor.web.util.Tokenizer;
import com.google.common.collect.ImmutableSet;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ReviewCategory {

    private static final Tokenizer tokenizer = new Tokenizer();

    private static final String WHITESPACE = " ";

    private final Set<ArdocResult> reviews;

    private final String category;

    public ReviewCategory(Collection<ArdocResult> reviews, String category) {
        this.reviews = ImmutableSet.copyOf(reviews);
        this.category = category;
    }

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

    public Document<String> asDocument() {
        List<String> tokens = tokenizer.tokenize(aggregateReviewsIntoDocument());
        return new Document<>(tokens);
    }

    public Document<List<String>> asDocument(int n) {
        List<List<String>> tokenize = tokenizer.tokenize(aggregateReviewsIntoDocument(), n);
        return new Document<>(tokenize);
    }

    private String aggregateReviewsIntoDocument() {
        List<String> sentences = reviews.stream().map(ArdocResult::getSentence).collect(Collectors.toList());
        return String.join(WHITESPACE, sentences);
    }
}
