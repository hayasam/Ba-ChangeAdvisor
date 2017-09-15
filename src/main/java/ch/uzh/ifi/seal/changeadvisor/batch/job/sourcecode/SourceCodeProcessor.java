package ch.uzh.ifi.seal.changeadvisor.batch.job.sourcecode;

import ch.uzh.ifi.seal.changeadvisor.preprocessing.CorpusProcessor;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.ClassBean;
import org.springframework.batch.item.ItemProcessor;

import java.util.Set;

/**
 * Created by alex on 22.07.2017.
 */
public class SourceCodeProcessor implements ItemProcessor<ClassBean, CodeElement> {

    private CorpusProcessor corpusProcessor;

    public SourceCodeProcessor(CorpusProcessor corpusProcessor) {
        this.corpusProcessor = corpusProcessor;
    }

    @Override
    public CodeElement process(ClassBean item) throws Exception {
        Set<String> bag = corpusProcessor.transform(item.getPublicCorpus());
        if (bag.isEmpty()) {
            return null;
        }
        return new CodeElement(item.getFullyQualifiedClassName(), bag);
    }
}
