package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.apache.log4j.Logger;
import org.ardoc.Parser;
import org.ardoc.UnknownCombinationException;
import org.springframework.batch.item.ItemProcessor;

/**
 * Processor for app reviews.
 * Created by alex on 17.07.2017.
 */
public class ArdocProcessor implements ItemProcessor<String, ArdocResult> {

    private static final Logger logger = Logger.getLogger(ArdocProcessor.class);

    private static final Parser parser = Parser.getInstance();

    private static final String ARDOC_METHODS = "NLP+TA+SA";

    private static int COUNTER = 0;

    @Override
    public ArdocResult process(String item) throws UnknownCombinationException {
        ArdocResult result = new ArdocResult(parser.extract(ARDOC_METHODS, item));
        trackProgress();
        return result;
    }

    private void trackProgress() {
        COUNTER += 1;
        if (COUNTER % 10 == 0) {
            logger.info(String.format("Ardoc: Finished processing %d lines.", COUNTER++));
        }
    }
}