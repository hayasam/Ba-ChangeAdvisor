package ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.TransformedFeedback;
import com.google.common.collect.Sets;
import org.ardoc.Result;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;

/**
 * Created by alex on 20.07.2017.
 */
public class FeedbackProcessorTest {

    @Test
    public void process() throws Exception {
        Result result = Mockito.mock(Result.class);
        when(result.getSentence()).thenReturn("My only complaint is that I'd like to organize things more, remove or add album pics, rearrange things, etc.");
        ArdocResult ardocResult = new ArdocResult(result);

        FeedbackProcessor processor = new FeedbackProcessor();

        TransformedFeedback transformedFeedback = processor.process(ardocResult);

        final Set<String> pocResults = Sets.newHashSet("add rearrang complaint etc organ remov".split(" "));
        final Set<String> expectedResults = Sets.newHashSet("complaint", "organ", "remov", "add", "album", "pic", "rearrang");
        final Set<String> results = transformedFeedback.getBagOfWords();

        List<String> pocSorted = new ArrayList<>(pocResults);
        List<String> resultsSorted = new ArrayList<>(results);
        Collections.sort(pocSorted);
        Collections.sort(resultsSorted);

        Assert.assertThat(expectedResults.size(), is(results.size()));
        for (String s : results) {
            Assert.assertTrue(String.format("Collection does not contain: %s", s), expectedResults.contains(s));
        }
    }

}