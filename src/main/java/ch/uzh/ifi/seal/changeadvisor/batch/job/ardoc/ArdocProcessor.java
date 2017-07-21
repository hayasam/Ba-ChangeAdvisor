package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.apache.log4j.Logger;
import org.ardoc.Parser;
import org.ardoc.UnknownCombinationException;
import org.springframework.batch.item.ItemProcessor;

/**
 * Processor for app reviews.
 * Created by alex on 17.07.2017.
 */
public class ArdocProcessor implements ItemProcessor<String, ArdocResults> {

    private static final Logger logger = Logger.getLogger(ArdocProcessor.class);

    private static final Parser parser = Parser.getInstance();

    private static final String ARDOC_METHODS = "NLP+SA";

    private int counter = 0;

    @Override
    public ArdocResults process(String item) throws UnknownCombinationException {
        ArdocResults result = new ArdocResults(parser.extract(ARDOC_METHODS, item));
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
