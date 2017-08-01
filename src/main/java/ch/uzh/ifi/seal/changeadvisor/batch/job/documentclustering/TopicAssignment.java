package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by alex on 24.07.2017.
 */
@Document
public class TopicAssignment {

    @Id
    private String id;

    private final String originalSentence;

    private final Set<String> bag;

    private final int topic;

    private LocalDateTime timestamp;

    public TopicAssignment(String originalSentence, Set<String> bag, int topic) {
        this.originalSentence = originalSentence;
        this.bag = bag;
        this.topic = topic;
        timestamp = LocalDateTime.now();
    }

    public TopicAssignment(Set<String> bag, int topic) {
        this.originalSentence = "";
        this.bag = bag;
        this.topic = topic;
        timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getOriginalSentence() {
        return originalSentence;
    }

    public Set<String> getBag() {
        return bag;
    }

    public int getTopic() {
        return topic;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "TopicAssignment{" +
                "originalSentence='" + originalSentence + '\'' +
                ", bag=" + bag +
                ", topic=" + topic +
                ", timestamp=" + timestamp +
                '}';
    }
}
