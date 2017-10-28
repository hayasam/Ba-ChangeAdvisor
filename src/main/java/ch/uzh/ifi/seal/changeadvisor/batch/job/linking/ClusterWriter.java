package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Writes results of clustering process.
 * Can write both the results of bulk processing and one-cluster-at-a-time processing.
 */
@Component
public class ClusterWriter implements ItemWriter<List<LinkingResult>> {

    private final LinkingResultRepository repository;

    @Autowired
    public ClusterWriter(LinkingResultRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(List<? extends List<LinkingResult>> items) throws Exception {
        items.forEach(repository::saveAll);
    }
}
