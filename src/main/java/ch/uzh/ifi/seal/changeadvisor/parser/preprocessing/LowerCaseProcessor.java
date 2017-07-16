package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

/**
 * Created by alexanderhofmann on 16.07.17.
 */
public class LowerCaseProcessor extends ProcessingStep {

    @Override
    public String handle(String text) {
        text = text.toLowerCase();
        return next(text);
    }
}
