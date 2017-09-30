package ch.uzh.ifi.seal.changeadvisor.web.dto;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.web.util.Document;
import ch.uzh.ifi.seal.changeadvisor.web.util.NGram;
import ch.uzh.ifi.seal.changeadvisor.web.util.Tokenizer;
import ch.uzh.ifi.seal.changeadvisor.web.util.Unigram;
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

    public Document asDocument(int n) {
        if (n == 1) {
            List<String> tokens = tokenizer.tokenize(aggregateReviewsIntoDocument());
            List<Unigram> unigrams = tokens.stream().map(Unigram::new).collect(Collectors.toList());
            return new Document(unigrams);
        } else {
            List<List<String>> tokens = tokenizer.tokenize(aggregateReviewsIntoDocument(), n);
            List<NGram> ngrams = tokens.stream().map(NGram::new).collect(Collectors.toList());
            return new Document(ngrams);
        }
    }

    private String aggregateReviewsIntoDocument() {
        List<String> sentences = reviews.stream().map(ArdocResult::getSentence).collect(Collectors.toList());
        return String.join(WHITESPACE, sentences);
    }
}
