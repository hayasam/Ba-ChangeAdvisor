package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.ml.Corpus;
import ch.uzh.ifi.seal.changeadvisor.ml.DocumentClusterer;
import ch.uzh.ifi.seal.changeadvisor.ml.DocumentClustererAdapter;
import org.springframework.batch.item.ItemProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by alex on 24.07.2017.
 */
public class TopicClustering implements ItemProcessor<List<TransformedFeedback>, TopicClusteringResult> {

    private DocumentClusterer documentClusterer;

    private final int maxIterations;

    public TopicClustering(int maxIterations) {
        this.maxIterations = maxIterations;
        documentClusterer = new DocumentClustererAdapter();
    }

    @Override
    public TopicClusteringResult process(List<TransformedFeedback> items) throws Exception {
        List<List<String>> documents = items.stream().map(f -> new ArrayList<>(f.getBagOfWords())).collect(Collectors.toList());
        List<String> originalSentences = items.stream().map(TransformedFeedback::getSentence).collect(Collectors.toList());
        Corpus corpus = new Corpus(originalSentences, documents);
        documentClusterer.fit(corpus, maxIterations);

        List<TopicAssignment> topics = documentClusterer.assignments();
        List<Topic> assignments = documentClusterer.topics();
        return new TopicClusteringResult(assignments, topics);
    }
}
