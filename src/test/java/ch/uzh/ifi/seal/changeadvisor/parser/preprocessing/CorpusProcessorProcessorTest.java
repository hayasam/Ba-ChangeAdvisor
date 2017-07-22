package ch.uzh.ifi.seal.changeadvisor.parser.preprocessing;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;


/**
 * Created by alexanderhofmann on 16.07.17.
 */
public class CorpusProcessorProcessorTest {

    @Test
    public void builder() throws Exception {
        String testText = "Example:**\n" +
                "Do you really think it is weakness that yields to temptation? I tell you that there are terrible temptations which it requires strength, strength and courage to yield to ~ Oscar Wilde\n";

        CorpusProcessor corpusProcessor = new CorpusProcessor.Builder()
                .escapeSpecialChars()
                .lowerCase()
                .stem()
                .removeStopWords()
                .removeTokensShorterThan(3)
                .build();

        Set<String> processed = corpusProcessor.transform(testText);

        String stemmedTestText = "Exampl weak yield temptat tell terribl temptat requir strength strength courag yield Oscar Wild".toLowerCase();
        Set<String> tokens = ImmutableSet.copyOf(Splitter.on(" ").omitEmptyStrings().trimResults().split(stemmedTestText));

        List<String> pSort = new ArrayList<>(processed);
        List<String> tSort = new ArrayList<>(tokens);
        Collections.sort(pSort);
        Collections.sort(tSort);

        Assert.assertThat(processed.size(), is(tokens.size()));
        for (String token : tokens) {
            Assert.assertTrue(processed.contains(token));
        }
    }
}