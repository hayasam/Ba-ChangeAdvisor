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
                findCandidates(cluster.getValue(), codeComponentWords, candidates, clusterBag);

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

    @Override
    public List<LinkingResult> process(int topicId, Collection<TopicAssignment> assignments, Collection<CodeElement> codeElements) {
        Assert.notNull(similarityMetric, "No similarity metric set!");

        List<LinkingResult> results = new ArrayList<>(assignments.size());

        if (topicId != 0) {

            Collection<CodeElement> candidates = new HashSet<>();
            Set<String> clusterBag = new HashSet<>();

            // Find candidates
            findCandidates(assignments, codeElements, candidates, clusterBag);

            final Set<String> clusterCleanedBag = corpusProcessor.transform(clusterBag);

            logger.debug(String.format("Cluster: %d, size: %d", topicId, assignments.size()));
            logger.debug(String.format("Candidates size: %d", candidates.size()));

            List<LinkingResult> similarityResults = checkSimilarity(topicId, candidates, clusterCleanedBag);
            results.addAll(similarityResults);

            logger.info(String.format("Finished running topic: %d", topicId));
        }
        return results;
    }

    private List<LinkingResult> checkSimilarity(int topicId, Collection<CodeElement> candidates, Set<String> clusterBag) {
        List<LinkingResult> results = new ArrayList<>();

        for (CodeElement candidate : candidates) {
            Optional<LinkingResult> result = checkSimilarity(topicId, candidate, clusterBag);
            result.ifPresent(results::add);
        }
        return results;
    }

    private Optional<LinkingResult> checkSimilarity(int topicId, CodeElement candidate, Set<String> clusterBag) {
        final Set<String> codeElementBag = corpusProcessor.transform(candidate.getBag());

        if (!clusterBag.isEmpty() && !codeElementBag.isEmpty()) {

            // Compute asymmetric dice index.
            double similarity = similarityMetric.similarity(clusterBag, codeElementBag);

            if (similarity >= 0.5) {
                LinkingResult result = new LinkingResult(
                        topicId, clusterBag, codeElementBag,
                        candidate.getFullyQualifiedClassName(), similarity);
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    private void findCandidates(Collection<TopicAssignment> assignments, Collection<CodeElement> elements, Collection<CodeElement> candidates, Set<String> clusterBag) {
        for (TopicAssignment review : assignments) {
            Set<String> reviewWords = review.getBag();

            for (CodeElement codeElement : elements) {
                Set<String> intersection = Sets.intersection(reviewWords, codeElement.getBag());

                if (!intersection.isEmpty()) {
                    clusterBag.addAll(review.getBag());
                    candidates.add(codeElement);
                }
            }
        }
    }

    private void findCandidates(Collection<TopicAssignment> assignments, Map<CodeElement, Set<String>> codeComponentWords, Collection<CodeElement> candidates, Set<String> clusterBag) {
        for (TopicAssignment review : assignments) {
            Set<String> reviewWords = review.getBag();

            for (Map.Entry<CodeElement, Set<String>> codeElement : codeComponentWords.entrySet()) {
                Set<String> intersection = Sets.intersection(reviewWords, codeElement.getValue());

                if (!intersection.isEmpty()) {
                    clusterBag.addAll(review.getBag());
                    candidates.add(codeElement.getKey());
                }
            }
        }
    }

    Map<Integer, List<TopicAssignment>> groupByTopic(Collection<TopicAssignment> assignments) {
        return assignments.stream().collect(Collectors.groupingBy(TopicAssignment::getTopic));
    }

    Map<CodeElement, Set<String>> codeComponentWordMap(Collection<CodeElement> codeElements) {
        return codeElements.stream().filter(c -> c != null && c.getBag() != null).collect(Collectors.toMap(Function.identity(), CodeElement::getBag));
    }
}
