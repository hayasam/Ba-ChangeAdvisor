package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.parser.BagOfWords;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.ClassBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * Processes a ClassBean into a BagOfWords.
 * Created by alex on 15.07.2017.
 *
 * @see BagOfWords#fromCorpus(String, String)
 */
public class BagOfWordsProcessor implements ItemProcessor<ClassBean, BagOfWords> {

    private static final Logger logger = LoggerFactory.getLogger(BagOfWordsProcessor.class);

    @Override
    public BagOfWords process(ClassBean item) throws Exception {
        final String fqcn = item.getFullyQualifiedClassName();
        final String corpus = item.getPublicCorpus();
        logger.debug("Converting corpus from component: " + fqcn);
        return BagOfWords.fromCorpus(fqcn, corpus);
    }
}
