package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.ardoc.Result;

import java.time.LocalDateTime;

/**
 * Represents a single result from the Ardoc parser timestamped with its creation time.
 * Created by alex on 17.07.2017.
 */
@SuppressWarnings("unused")
public class ArdocResult {

    private Result result;

    private LocalDateTime timestamp;

    public ArdocResult(Result result) {
        this.result = result;
        this.timestamp = LocalDateTime.now();
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
