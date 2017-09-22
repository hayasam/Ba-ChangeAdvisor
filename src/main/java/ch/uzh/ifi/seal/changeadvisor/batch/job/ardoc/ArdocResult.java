package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import org.ardoc.Result;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Represents a single result from the Ardoc parser timestamped with its creation time.
 * Created by alex on 17.07.2017.
 */
@SuppressWarnings("unused")
@Document(collection = "ardoc")
public class ArdocResult {

    @Id
    private String id;

    private String appName;

    private String heuristic;

    private int classification;

    private String sentence;

    private String category;

    private int sentimentClass;

    private Review review;

    private LocalDateTime timestamp;

    public ArdocResult() {
    }

    public ArdocResult(Review review, Result result) {
        this.review = review;
        this.appName = review != null ? review.getAppName() : "";
        this.sentence = result.getSentence();
        this.category = result.getSentenceClass();
        this.sentimentClass = result.getSentimentClass();
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Review getReview() {
        return review;
    }

    public Date getReviewDate() {
        if (review != null) {
            return review.getReviewDate();
        }
        return new Date(0);
    }

    public String getHeuristic() {
        return heuristic;
    }

    public int getClassification() {
        return classification;
    }

    public String getSentence() {
        return sentence;
    }

    public String getCategory() {
        return category;
    }

    public int getSentimentClass() {
        return sentimentClass;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public void setHeuristic(String heuristic) {
        this.heuristic = heuristic;
    }

    public void setClassification(int classification) {
        this.classification = classification;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setSentimentClass(int sentimentClass) {
        this.sentimentClass = sentimentClass;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return "ArdocResult{" +
                "id='" + id + '\'' +
                ", heuristic='" + heuristic + '\'' +
                ", classification=" + classification +
                ", sentence='" + sentence + '\'' +
                ", category='" + category + '\'' +
                ", sentimentClass=" + sentimentClass +
                ", timestamp=" + timestamp +
                '}';
    }
}
