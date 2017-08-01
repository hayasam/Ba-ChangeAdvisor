package ch.uzh.ifi.seal.changeadvisor.batch.job.bagofwords;

import ch.uzh.ifi.seal.changeadvisor.parser.BagOfWords;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.ClassBean;
import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.CorpusProcessor;
import org.springframework.batch.item.ItemProcessor;

import java.util.Set;

/**
 * Created by alex on 22.07.2017.
 */
public class SourceCodeProcessor implements ItemProcessor<ClassBean, BagOfWords> {

    private CorpusProcessor corpusProcessor;

    public SourceCodeProcessor(CorpusProcessor corpusProcessor) {
        this.corpusProcessor = corpusProcessor;
    }

    @Override
    public BagOfWords process(ClassBean item) throws Exception {
        Set<String> bag = corpusProcessor.transform(item.getPublicCorpus());
        if (bag.isEmpty()) {
            return null;
        }
        return new BagOfWords(item.getFullyQualifiedClassName(), bag);
    }
}