package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicClusteringResult;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;

public class LinkingProcessor implements ItemProcessor<TopicClusteringResult, List<LinkingResult>> {

    private Linker linker;

    public LinkingProcessor(Linker linker) {
        this.linker = linker;
    }

    @Override
    public List<LinkingResult> process(TopicClusteringResult item) throws Exception {
        return linker.process(item.getTopics(), item.getAssignments());
    }
}
