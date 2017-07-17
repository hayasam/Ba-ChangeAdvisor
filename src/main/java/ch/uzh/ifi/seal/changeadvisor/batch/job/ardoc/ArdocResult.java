package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.ardoc.Result;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents a result from the Ardoc parser timestamped with its creation time.
 * Created by alex on 17.07.2017.
 */
@SuppressWarnings("unused")
public class ArdocResult {

    private List<Result> results;

    private LocalDateTime timestamp;

    public ArdocResult(List<Result> results) {
        this.results = results;
        this.timestamp = LocalDateTime.now();
    }

    public List<Result> getResults() {
        return results;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ArdocResult{" +
                "results=" + results +
                ", timestamp=" + timestamp +
                '}';
    }
}
