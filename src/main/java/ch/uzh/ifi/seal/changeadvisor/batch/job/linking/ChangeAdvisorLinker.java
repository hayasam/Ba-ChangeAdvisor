package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics.AsymmetricDiceIndex;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics.SimilarityMetric;
import ch.uzh.ifi.seal.changeadvisor.parser.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.ComposedIdentifierSplitter;
import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.CorpusProcessor;
import edu.stanford.nlp.util.Sets;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ChangeAdvisorLinker implements Linker {

    private static final Logger logger = Logger.getLogger(ChangeAdvisorLinker.class);

    private SimilarityMetric similarityMetric = new AsymmetricDiceIndex();

    private CorpusProcessor corpusProcessor = new CorpusProcessor.Builder()
            .escapeSpecialChars()
            .withComposedIdentifierSplit(new ComposedIdentifierSplitter())
            .removeTokensShorterThan(4)
            .build();

    @Override
    public List<LinkingResult> process(Collection<TopicAssignment> assignments, Collection<CodeElement> codeElements) {
        Assert.notNull(similarityMetric, "No similarity metric set!");

        Map<Integer, List<TopicAssignment>> clusters = groupByTopic(assignments);
        Map<CodeElement, Set<String>> codeComponentWords = codeComponentWordMap(codeElements);

        List<LinkingResult> results = new ArrayList<>(assignments.size());
        for (Map.Entry<Integer, List<TopicAssignment>> cluster : clusters.entrySet()) {
            if (!cluster.getKey().equals(0)) {

                Collection<CodeElement> candidates = new HashSet<>();

                Set<String> clusterBag = new HashSet<>();

                // Find candidates
                for (TopicAssignment review : cluster.getValue()) {
                    Set<String> reviewWords = review.getBag();

                    for (Map.Entry<CodeElement, Set<String>> codeElement : codeComponentWords.entrySet()) {
                        Set<String> intersection = Sets.intersection(reviewWords, codeElement.getValue());

                        if (!intersection.isEmpty()) {
                            clusterBag.addAll(review.getBag());
                            candidates.add(codeElement.getKey());
                        }
                    }
                }

                final Set<String> clusterCleanedBag = corpusProcessor.transform(clusterBag);

                logger.debug(String.format("Cluster: %d, size: %d", cluster.getKey(), cluster.getValue().size()));
                logger.debug(String.format("Candidates size: %d", candidates.size()));

                for (CodeElement codeElement : candidates) {
                    final Set<String> codeElementBag = corpusProcessor.transform(codeElement.getBag());

                    if (!clusterCleanedBag.isEmpty() && !codeElementBag.isEmpty()) {

                        // Compute asymmetric dice index.
                        double similarity = similarityMetric.similarity(clusterCleanedBag, codeElementBag);

                        if (similarity >= 0.5) {
                            LinkingResult result = new LinkingResult(
                                    cluster.getKey(), clusterCleanedBag, codeElementBag,
                                    codeElement.getFullyQualifiedClassName(), similarity);
                            results.add(result);
                        }
                    }
                }

                logger.info(String.format("Finished running topic: %d", cluster.getKey()));
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
