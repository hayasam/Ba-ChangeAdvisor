package ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by alex on 20.07.2017.
 */
@Document
public class TransformedFeedback {

    @Id
    private String id;

    private ArdocResult ardocResult;

    private Set<String> bagOfWords;

    private LocalDateTime timestamp;

    public TransformedFeedback() {
    }

    public TransformedFeedback(ArdocResult ardocResult, Set<String> tokens) {
        this.ardocResult = ardocResult;
        this.bagOfWords = tokens;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public ArdocResult getArdocResult() {
        return ardocResult;
    }

    public Set<String> getBagOfWords() {
        return bagOfWords;
    }

    public String getBagAsString() {
        return String.join(" ", bagOfWords);
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getSentence() {
        return ardocResult.getSentence();
    }

    public String getCategory() {
        return ardocResult.getCategory();
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setArdocResult(ArdocResult ardocResult) {
        this.ardocResult = ardocResult;
    }

    public void setBagOfWords(Set<String> bagOfWords) {
        this.bagOfWords = bagOfWords;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "TransformedFeedback{" +
                "sentence=" + ardocResult.getSentence() +
                ", bagOfWords=" + bagOfWords +
                '}';
    }
}
