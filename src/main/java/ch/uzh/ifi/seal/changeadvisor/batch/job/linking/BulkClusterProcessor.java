package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicClusteringResult;
import ch.uzh.ifi.seal.changeadvisor.parser.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.parser.CodeElementRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Processes documents clusters and processed code components to find similarities.
 * Works in bulk by operating on all clusters.
 */
@Component
public class BulkClusterProcessor implements ItemProcessor<TopicClusteringResult, List<LinkingResult>> {

    private Linker linker;

    private CodeElementRepository codeElementRepository;

    private List<CodeElement> codeElements;

    @Autowired
    public BulkClusterProcessor(Linker changeAdvisorLinker, CodeElementRepository codeElementRepository) {
        this.linker = changeAdvisorLinker;
        this.codeElementRepository = codeElementRepository;
        codeElements = getCodeElements();
    }

    private List<CodeElement> getCodeElements() {
        return codeElementRepository.findAll();
    }

    @Override
    public List<LinkingResult> process(TopicClusteringResult item) throws Exception {
        return linker.process(item.getAssignments(), codeElements);
    }
}
