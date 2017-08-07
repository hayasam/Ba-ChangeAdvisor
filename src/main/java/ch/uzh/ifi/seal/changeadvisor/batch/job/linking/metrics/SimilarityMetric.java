package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import java.util.Collection;

public interface SimilarityMetric {

    /**
     * Compute similarity between two corpuses.
     *
     * @param document1 review text.
     * @param document2 code element text.
     * @return
     */
    double similarity(String document1, String document2);

    /**
     * Compute similarity between two corpuses as bags.
     *
     * @param document1 review text.
     * @param document2 code element text.
     * @return
     */
    double similarity(Collection<String> document1, Collection<String> document2);
}
