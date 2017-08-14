package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.ardoc.Result;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Represents a single result from the Ardoc parser timestamped with its creation time.
 * Created by alex on 17.07.2017.
 */
@SuppressWarnings("unused")
@Document(collection = "ardoc")
public class ArdocResult {

    @Id
    private String id;

    private Result result;

    private LocalDateTime timestamp;

    public ArdocResult() {
    }

    public ArdocResult(Result result) {
        this.result = result;
        this.timestamp = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Result getResult() {
        return result;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ArdocResult{" +
                "result=" + result +
                ", timestamp=" + timestamp +
                '}';
    }
}
