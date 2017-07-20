package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * Created by alex on 20.07.2017.
 */
public class TransformedFeedback {

    private ArdocResult ardocResult;

    private Set<String> bagOfWords;

    private LocalDateTime timestamp;

    public TransformedFeedback(ArdocResult ardocResult, Set<String> tokens) {
        this.ardocResult = ardocResult;
        this.bagOfWords = tokens;
        this.timestamp = LocalDateTime.now();
    }

    public ArdocResult getArdocResult() {
        return ardocResult;
    }

    public Set<String> getBagOfWords() {
        return bagOfWords;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
