package ch.uzh.ifi.seal.changeadvisor.ml;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.opencsv.CSVReader;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class HierarchicalDirichletProcessTest {

    private static final String FILE_PATH = "test_files_parser/transformed_feedback/feedback.csv";

    private static final Logger logger = Logger.getLogger(HierarchicalDirichletProcessTest.class);

    private List<String> inputCategories = Lists.newArrayList("FEATURE REQUEST", "PROBLEM DISCOVERY");

    private Corpus corpus;

    @Before
    public void setUp() throws Exception {
        CSVReader reader = new CSVReader(new FileReader(FILE_PATH));

        corpus = new Corpus(new ArrayList<>());
        String[] line = reader.readNext();
        while ((line = reader.readNext()) != null) {
            if (line.length == 3 && inputCategories.contains(line[1])) {
                corpus.addDocument(Lists.newArrayList(Splitter.on(" ").omitEmptyStrings().trimResults().split(line[2])));
            }
        }
        logger.info(corpus.size());

    }

    @Test
    public void fit() throws Exception {
        HierarchicalDirichletProcess hdplda = new HierarchicalDirichletProcess(1.0, 0.5, 1.0);
        hdplda.fit(corpus, 10);
        List<TopicAssignment> topics = hdplda.topics();
        logger.info(topics);
    }

}