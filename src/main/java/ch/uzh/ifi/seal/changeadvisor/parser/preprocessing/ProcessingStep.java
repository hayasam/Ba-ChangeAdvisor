package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

/**
 * Defines an abstract processing step for processing a class corpus.
 * Each step is defined by its ability to handle a token and knowledge of the next step in the processing pipeline.
 * Created by alexanderhofmann on 16.07.17.
 */
public abstract class ProcessingStep {

    private ProcessingStep next;

    public abstract String handle(String text);

    public void setNext(ProcessingStep next) {
        this.next = next;
    }

    public String next(String text) {
        return next.handle(text);
    }
}
