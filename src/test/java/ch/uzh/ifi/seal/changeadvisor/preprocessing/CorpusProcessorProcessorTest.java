package ch.uzh.ifi.seal.changeadvisor.preprocessing;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

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

    @Test(expected = IllegalArgumentException.class)
    public void processorNull() throws Exception {
        new CorpusProcessor.Builder().build().transform((Collection<String>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void processorNull2() throws Exception {
        new CorpusProcessor.Builder().build().transform((String) null);
    }

    @Test
    public void processorEmptyString() throws Exception {
        CorpusProcessor processor = new CorpusProcessor.Builder().build();
        Set<String> transform = processor.transform("");
        Set<String> expected = Sets.newHashSet("");
        Assert.assertThat(transform, is(expected));
    }

    @Test
    public void processorEmptyString2() throws Exception {
        CorpusProcessor processor = new CorpusProcessor.Builder().build();
        Set<String> transform = processor.transform(Sets.newHashSet(""));
        Set<String> expected = Sets.newHashSet("");
        Assert.assertThat(transform, is(expected));
    }

    @Test
    public void composedIdentifier() throws Exception {
        CorpusProcessor processor = new CorpusProcessor.Builder()
                .withComposedIdentifierSplit()
                .build();
        String text = "camelCase normal words snake_case a1a";

        Set<String> transform = processor.transform(text);
        Set<String> expected = Sets.newHashSet("camel", "Case", "normal", "words", "snake", "case", "a", "1");

        Assert.assertThat(transform, is(expected));
    }

    @Test
    public void contractionExpander() throws Exception {
        CorpusProcessor processor = new CorpusProcessor.Builder()
                .withContractionExpander()
                .build();
        String text = "I'd like I hadn't It's normal token don't you think";

        Set<String> transform = processor.transform(text);
        Set<String> expected = Sets.newHashSet("i", "would", "like", "i", "I", "had", "not", "it", "is", "normal", "token", "do", "you", "think");

        Assert.assertThat(transform, is(expected));
    }

    @Test
    public void specialCharsEscape() throws Exception {
        CorpusProcessor processor = new CorpusProcessor.Builder()
                .escapeSpecialChars()
                .build();
        String text = "normal * ** ** ; . , ' ? \"\" token isn't";
        Set<String> transform = processor.transform(text);
        Set<String> expected = Sets.newHashSet("normal", "token", "isn", "t");

        Assert.assertThat(transform, is(expected));
    }

    @Test
    public void lowerCase() throws Exception {
        CorpusProcessor processor = new CorpusProcessor.Builder()
                .lowerCase()
                .build();
        String text = "CAPS LOCK ENGAGE LOLOLOL lowercase word";
        Set<String> transform = processor.transform(text);
        Set<String> expected = Sets.newHashSet("caps", "lock", "engage", "lololol", "lowercase", "word");

        Assert.assertThat(transform, is(expected));
    }

    @Test
    public void stopWords() throws Exception {
        CorpusProcessor processor = new CorpusProcessor.Builder()
                .removeStopWords()
                .build();
        String text = "this and THAT not a stopword public void";
        Set<String> transform = processor.transform(text);
        Set<String> expected = Sets.newHashSet("stopword");

        Assert.assertThat(transform, is(expected));
    }

    @Test
    public void shortTokens() throws Exception {
        CorpusProcessor processor = new CorpusProcessor.Builder()
                .removeTokensShorterThan(3)
                .build();
        String text = "a ab abc abcd b bc bcd bcde c cd cde cdef";
        Set<String> transform = processor.transform(text);
        Set<String> expected = Sets.newHashSet("abc", "abcd", "bcd", "bcde", "cde", "cdef");

        Assert.assertThat(transform, is(expected));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shortTokensTooShort() throws Exception {
        new CorpusProcessor.Builder()
                .removeTokensShorterThan(0)
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void shortTokensTooShort2() throws Exception {
        new CorpusProcessor.Builder()
                .removeTokensShorterThan(-1)
                .build();
    }
}
