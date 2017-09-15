package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Cluster;
import ch.uzh.ifi.seal.changeadvisor.source.parser.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.source.parser.CodeElementRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Processor for document clusters.
 * It processes one cluster at a time and needs all code element informations before starting.
 */
@Component
public class ClusterProcessor implements ItemProcessor<Cluster, List<LinkingResult>> {

    private Linker linker;

    private final CodeElementRepository codeElementRepository;

    private List<CodeElement> codeElements;

    @Autowired
    public ClusterProcessor(CodeElementRepository codeElementRepository, ChangeAdvisorLinker changeAdvisorLinker) {
        this.codeElementRepository = codeElementRepository;
        this.linker = changeAdvisorLinker;
    }

    @Override
    public List<LinkingResult> process(Cluster item) throws Exception {
        if (codeElements == null || codeElements.isEmpty()) {
            codeElements = codeElementRepository.findAll();
        }
        return linker.process(item.getTopicId(), item.getAssignments(), codeElements);
    }
}
