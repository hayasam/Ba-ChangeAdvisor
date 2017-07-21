package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;


/**
 * Created by alexanderhofmann on 16.07.17.
 */
public class CorpusProcessorTest {

    @Test
    public void builder() throws Exception {
        CorpusProcessor processor = new CorpusProcessor.Builder(true, new ComposedIdentifierSplitter())
                .step(new LowerCaseProcessor())
                .step(new Stemmer(Stemmer.DEFAULT_MINIMUM_WORD_LENGTH))
                .filter(StopWordFilter::isNotStopWord)
                .filter(t -> t.length() > 3)
                .build();

        String testText = "Example:**\n" +
                "Do you really think it is weakness that yields to temptation? I tell you that there are terrible temptations which it requires strength, strength and courage to yield to ~ Oscar Wilde\n";
        Set<String> processed = processor.process("default", testText).getBag();

        String stemmedTestText = "Exampl weak yield temptat tell terribl temptat requir strength strength courag yield Oscar Wild".toLowerCase();
        Set<String> tokens = ImmutableSet.copyOf(Splitter.on(" ").omitEmptyStrings().trimResults().split(stemmedTestText));

        List<String> pSort = new ArrayList<>(processed);
        List<String> tSort = new ArrayList<>(tokens);
        Collections.sort(pSort);
        Collections.sort(tSort);

        Assert.assertEquals(processed.size(), tokens.size());
        for (String token : tokens) {
            Assert.assertTrue(processed.contains(token));
        }
    }
}