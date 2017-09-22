package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import ch.uzh.ifi.seal.changeadvisor.batch.job.reviews.Review;
import org.apache.log4j.Logger;
import org.ardoc.Parser;
import org.ardoc.UnknownCombinationException;
import org.springframework.batch.item.ItemProcessor;

public class ReviewProcessor implements ItemProcessor<Review, ArdocResults> {

    private static final Logger logger = Logger.getLogger(ReviewProcessor.class);

    private static final Parser parser = Parser.getInstance();

    private static final String ARDOC_METHODS = "NLP+SA";

    private int counter = 0;

    @Override
    public ArdocResults process(Review item) throws UnknownCombinationException {
        ArdocResults result = new ArdocResults(item, parser.extract(ARDOC_METHODS, item.getReviewText()));
        trackProgress();
        return result;
    }

    private void trackProgress() {
        counter += 1;
        if (counter % 10 == 0) {
            logger.info(String.format("Ardoc: Finished processing %d lines.", counter));
        }
    }
}
