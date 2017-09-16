package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "Review")
public class Review {

    @Id
    private String id;

    private String appName;

    private String reviewText;

    private Date reviewDate;

    private int numberOfStars;

    public Review() {
    }

    public Review(String appName) {
        this.appName = appName;
    }

    public Review(String id, String appName, String reviewText, Date reviewDate, int numberOfStars) {
        this.id = id;
        this.appName = appName;
        this.reviewText = reviewText;
        this.reviewDate = reviewDate;
        this.numberOfStars = numberOfStars;
    }

    public String getId() {
        return id;
    }

    public String getAppName() {
        return appName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public Date getReviewDate() {
        return reviewDate;
    }

    public int getNumberOfStars() {
        return numberOfStars;
    }
}
