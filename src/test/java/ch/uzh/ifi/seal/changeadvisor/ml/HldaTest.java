package ch.uzh.ifi.seal.changeadvisor.ml;

import cc.mallet.pipe.*;
import cc.mallet.pipe.iterator.CsvIterator;
import cc.mallet.topics.HierarchicalLDA;
import cc.mallet.types.InstanceList;
import cc.mallet.util.Randoms;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class HldaTest {

    @Test
    public void testMallet() throws Exception {
//        CommandOption.setSummary(HierarchicalLDATUI.class, "Hierarchical LDA with a fixed tree depth.");
//        CommandOption.process(HierarchicalLDATUI.class, null);

        ArrayList<Pipe> pipeList = new ArrayList<Pipe>();

        // Pipes: lowercase, tokenize, map to features
        pipeList.add(new CharSequenceLowercase());
        pipeList.add(new CharSequence2TokenSequence(Pattern.compile("\\p{L}[\\p{L}\\p{P}]+\\p{L}")));
        pipeList.add(new TokenSequence2FeatureSequence());

        InstanceList instances = new InstanceList(new SerialPipes(pipeList));

        File inputFile = new File("/Users/alexanderhofmann/Dropbox/UZH/Bsc/Ba/changeadvisor_input/output/com.frostwire.android/transformed_feedback/com.frostwire.android.csv");
        Reader fileReader = new InputStreamReader(new FileInputStream(inputFile), "UTF-8");
        instances.addThruPipe(new CsvIterator(fileReader, Pattern.compile("^\"(.+)\",\"(.+)\",\"(.*)\"$"),
                3, 2, 1)); // data, label, name fields

        instances = instances.subList(0.8);
        InstanceList testing = instances.subList(0.2);

//        if (testingFile.value() != null) {
//            testing = InstanceList.load(new File(testingFile.value()));
//        }

        double alpha = 1.0;
        double beta = 0.5;
        double gamma = 1.0;
        HierarchicalLDA hlda = new HierarchicalLDA();
        hlda.setAlpha(alpha);
        hlda.setGamma(gamma);
        hlda.setEta(beta);

        hlda.setTopicDisplay(1, 10);
        hlda.setProgressDisplay(false);

        Randoms random = new Randoms();
        hlda.initialize(instances, testing, 5, random);
        hlda.estimate(3);
        hlda.printState(new PrintWriter(new File("Data.txt")));

//        ParallelTopicModel model = new ParallelTopicModel()


//        if (stateFile.value() != null) {
//            hlda.printState(new PrintWriter(stateFile.value()));
//        }
//        if (testing != null) {
//            double empiricalLikelihood = hlda.empiricalLikelihood(1000, testing);
//            System.out.println("Empirical likelihood: " + empiricalLikelihood);
//        }
    }
}
