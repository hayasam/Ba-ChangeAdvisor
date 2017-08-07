package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.Set;

public class AsymmetricDiceIndex implements SimilarityMetric {

    @Override
    public double similarity(String document1, String document2) {
        Set<String> doc1 = Sets.newHashSet(Splitter.on(" ").omitEmptyStrings().trimResults().split(document1));
        Set<String> doc2 = Sets.newHashSet(Splitter.on(" ").omitEmptyStrings().trimResults().split(document2));
        return similarity(doc1, doc2);
    }

    @Override
    public double similarity(Collection<String> document1, Collection<String> document2) {
        if (document1.isEmpty() || document2.isEmpty()) {
            return 0.0;
        }

        final int overlap = countOverlappingWords(document1, document2);
        Double result = (double) (2 * overlap);

        if (document2.size() < document1.size()) {
            result = result / (double) document2.size();
        } else {
            result = result / (double) document1.size();
        }

        if (result > 1.0) {
            result = 1.0;
        }

        if (result.toString().contains("E")) {
            result = 0.0;
        }

        return result;
    }

    int countOverlappingWords(Collection<String> doc1, Collection<String> doc2) {
        int counter = 0;
        for (String s1 : doc1) {
            if (doc2.contains(s1)) {
                counter++;
            }
        }
        return counter;
    }
}
