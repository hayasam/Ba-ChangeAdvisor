package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics.SimilarityMetric;
import ch.uzh.ifi.seal.changeadvisor.parser.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.ComposedIdentifierSplitter;
import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.CorpusProcessor;
import edu.stanford.nlp.util.Sets;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ChangeAdvisorLinker implements Linker {

    private SimilarityMetric similarityMetric;

    private CorpusProcessor corpusProcessor = new CorpusProcessor.Builder()
            .escapeSpecialChars()
            .withComposedIdentifierSplit(new ComposedIdentifierSplitter())
            .removeTokensShorterThan(4)
            .build();

    @Override
    public List<LinkingResult> process(Collection<Topic> topics, Collection<TopicAssignment> assignments, Collection<CodeElement> codeElements) {
        Assert.notNull(similarityMetric, "No similarity metric set!");

        Map<Integer, List<TopicAssignment>> clusters = groupByTopic(assignments);
        Map<CodeElement, Set<String>> codeComponentWords = codeComponentWordMap(codeElements);

        List<LinkingResult> results = new ArrayList<>(assignments.size());
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, List<TopicAssignment>> cluster : clusters.entrySet()) {
            if (!cluster.getKey().equals(0)) {

                Set<CodeElement> candidates = new HashSet<>();

                // Find candidates
                for (TopicAssignment review : cluster.getValue()) {
                    Set<String> reviewWords = review.getBag();

                    for (Map.Entry<CodeElement, Set<String>> codeElement : codeComponentWords.entrySet()) {
                        Set<String> intersection = Sets.intersection(reviewWords, codeElement.getValue());

                        if (!intersection.isEmpty()) {
                            sb.append(String.join(" ", review.getBag()));
                            candidates.add(codeElement.getKey());
                        }
                    }
                }

                //

                final Set<String> clusterCleanedBag = corpusProcessor.transform(sb.toString());
                for (CodeElement codeElement : candidates) {
                    final Set<String> codeElementBag = corpusProcessor.transform(codeElement.getBag());

                    if (!clusterCleanedBag.isEmpty() && !codeElementBag.isEmpty()) {
                        final String clusterText = String.join(" ", clusterCleanedBag);
                        final String codeElementText = String.join(" ", codeElementBag);

                        // Compute asymmetric dice index.
                        double similarity = similarityMetric.similarity(clusterText, codeElementText);

                        if (similarity >= 0.5) {
                            LinkingResult result = new LinkingResult(
                                    cluster.getKey(), clusterCleanedBag, codeElementBag,
                                    codeElement.getFullyQualifiedClassName(), similarity);
                            results.add(result);
                        }
                    }
                }

            }
        }
        return results;
    }

    Map<Integer, List<TopicAssignment>> groupByTopic(Collection<TopicAssignment> assignments) {
        return assignments.stream().collect(Collectors.groupingBy(TopicAssignment::getTopic));
    }

    Map<CodeElement, Set<String>> codeComponentWordMap(Collection<CodeElement> codeElements) {
        return codeElements.stream().filter(c -> c != null && c.getBag() != null).collect(Collectors.toMap(Function.identity(), CodeElement::getBag));
    }
}