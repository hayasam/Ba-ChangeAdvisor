package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import java.util.Set;

public class AsymmetricDiceIndex implements SimilarityMetric {

    @Override
    public double similarity(String document1, String document2) {

        return 0;
    }

    int countOverlappingWords(Set<String> doc1, Set<String> doc2) {
        int counter = 0;
        for (String s1 : doc1) {
            if (doc2.contains(s1)) {
                counter++;
            }
        }
        return counter;
    }
}
