package ch.uzh.ifi.seal.changeadvisor;

import org.apache.log4j.Logger;
import org.ardoc.Parser;
import org.ardoc.Result;
import org.ardoc.UnknownCombinationException;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Created by alexanderhofmann on 13.07.17.
 */
public class ArdocTest {

    private static final Logger logger = Logger.getLogger(ArdocTest.class);

    @Test
    public void ardocTest() throws UnknownCombinationException {
        String example = "You should add new levels for the game. " +
                "These annoying ads block the whole au, please fix. ";
        Parser p = Parser.getInstance();
        ArrayList<Result> res = p.extract("NLP+SA", example);
        for (Result r : res) {
            logger.info(r.getSentence() + "-" + r.getSentenceClass() + "-" + r.getSentimentClass());
        }
    }
}
