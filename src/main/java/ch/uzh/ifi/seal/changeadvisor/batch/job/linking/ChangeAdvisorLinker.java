package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics.AsymmetricDiceIndex;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics.SimilarityMetric;
import ch.uzh.ifi.seal.changeadvisor.preprocessing.CorpusProcessor;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;
import com.google.common.collect.ImmutableSet;
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
            .withComposedIdentifierSplit()
            .removeTokensShorterThan(4)
            .build();

    public void setSimilarityMetric(SimilarityMetric similarityMetric) {
        this.similarityMetric = similarityMetric;
    }

    public void setCorpusProcessor(CorpusProcessor corpusProcessor) {
        this.corpusProcessor = corpusProcessor;
    }

    @Override
    public List<LinkingResult> process(Collection<TopicAssignment> assignments, Collection<CodeElement> codeElements) {
        Assert.notNull(similarityMetric, "No similarity metric set!");

        Map<Integer, List<TopicAssignment>> clusters = groupByTopic(assignments);
        Map<CodeElement, Collection<String>> codeComponentWords = codeComponentWordMap(codeElements);

        List<LinkingResult> results = new ArrayList<>(assignments.size());
        for (Map.Entry<Integer, List<TopicAssignment>> cluster : clusters.entrySet()) {
            if (!cluster.getKey().equals(0)) {

                Collection<CodeElement> candidates = new HashSet<>();
                Set<String> clusterBag = new HashSet<>();
                Set<String> originalReviews = new HashSet<>();

                // Find candidates
                findCandidates(cluster.getValue(), codeComponentWords, candidates, clusterBag, originalReviews);

                final Collection<String> clusterCleanedBag = corpusProcessor.transform(clusterBag);

                logger.debug(String.format("Cluster: %d, size: %d", cluster.getKey(), cluster.getValue().size()));
                logger.debug(String.format("Candidates size: %d", candidates.size()));

                for (CodeElement codeElement : candidates) {
                    final Collection<String> codeElementBag = corpusProcessor.transform(codeElement.getBag());

                    if (!clusterCleanedBag.isEmpty() && !codeElementBag.isEmpty()) {

                        // Compute asymmetric dice index.
                        double similarity = similarityMetric.similarity(clusterCleanedBag, codeElementBag);

                        if (similarity >= 0.5) {
                            LinkingResult result = new LinkingResult(
                                    cluster.getKey(), originalReviews, clusterCleanedBag, codeElementBag,
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
    public List<LinkingResult> process(int topicId, Collection<? extends LinkableReview> assignments, Collection<CodeElement> codeElements) {
        Assert.notNull(similarityMetric, "No similarity metric set!");

        List<LinkingResult> results = new ArrayList<>(assignments.size());

        if (topicId != 0) {

            Collection<CodeElement> candidates = new HashSet<>();
            Set<String> clusterBag = new HashSet<>();
            Set<String> originalReviews = new HashSet<>();

            findCandidates(assignments, codeElements, candidates, clusterBag, originalReviews);

            final Collection<String> clusterCleanedBag = corpusProcessor.transform(clusterBag);

            logger.debug(String.format("Cluster: %d, size: %d", topicId, assignments.size()));
            logger.debug(String.format("Candidates size: %d", candidates.size()));

            List<LinkingResult> similarityResults = checkSimilarity(topicId, candidates, clusterCleanedBag, originalReviews);
            results.addAll(similarityResults);

            logger.info(String.format("Finished running topic: %d", topicId));
        }
        return results;
    }

    private List<LinkingResult> checkSimilarity(int topicId, Collection<CodeElement> candidates, Collection<String> clusterBag, Collection<String> reviews) {
        List<LinkingResult> results = new ArrayList<>();

        for (CodeElement candidate : candidates) {
            Optional<LinkingResult> result = checkSimilarity(topicId, candidate, clusterBag, reviews);
            result.ifPresent(results::add);
        }
        return results;
    }

    private Optional<LinkingResult> checkSimilarity(int topicId, CodeElement candidate, Collection<String> clusterBag, Collection<String> reviews) {
        final Collection<String> codeElementBag = corpusProcessor.transform(candidate.getBag());

        if (!clusterBag.isEmpty() && !codeElementBag.isEmpty()) {

            // Compute asymmetric dice index.
            double similarity = similarityMetric.similarity(clusterBag, codeElementBag);

            if (similarity >= 0.5) {
                LinkingResult result = new LinkingResult(
                        topicId, reviews, clusterBag, codeElementBag,
                        candidate.getFullyQualifiedClassName(), similarity);
                return Optional.of(result);
            }
        }
        return Optional.empty();
    }

    private void findCandidates(Collection<? extends LinkableReview> assignments, Collection<CodeElement> elements,
                                Collection<CodeElement> candidates, Set<String> clusterBag, Set<String> originalReviews) {
        for (LinkableReview review : assignments) {
            Set<String> reviewWords = review.getBag();

            for (CodeElement codeElement : elements) {
                Collection<String> intersection = intersection(reviewWords, codeElement.getBag());
                if (!intersection.isEmpty()) {
                    clusterBag.addAll(review.getBag());
                    candidates.add(codeElement);
                    originalReviews.add(review.getOriginalSentence());
                }
            }
        }
    }

    private <T> Collection<T> intersection(Collection<T> c1, Collection<T> c2) {
        Set<T> set = new HashSet<>();
        set.addAll(c1);
        set.retainAll(c2);
        return ImmutableSet.copyOf(set);
    }

    private void findCandidates(Collection<TopicAssignment> assignments, Map<CodeElement, Collection<String>> codeComponentWords, Collection<CodeElement> candidates, Set<String> clusterBag, Set<String> originalReviews) {
        for (TopicAssignment review : assignments) {
            Set<String> reviewWords = review.getBag();

            for (Map.Entry<CodeElement, Collection<String>> codeElement : codeComponentWords.entrySet()) {
                Collection<String> intersection = intersection(reviewWords, codeElement.getValue());

                if (!intersection.isEmpty()) {
                    clusterBag.addAll(review.getBag());
                    candidates.add(codeElement.getKey());
                    originalReviews.add(review.getOriginalSentence());
                }
            }
        }
    }

    Map<Integer, List<TopicAssignment>> groupByTopic(Collection<TopicAssignment> assignments) {
        return assignments.stream().collect(Collectors.groupingBy(TopicAssignment::getTopic));
    }

    Map<CodeElement, Collection<String>> codeComponentWordMap(Collection<CodeElement> codeElements) {
        return codeElements.stream().filter(c -> c != null && c.getBag() != null).collect(Collectors.toMap(Function.identity(), CodeElement::getBag));
    }
}
