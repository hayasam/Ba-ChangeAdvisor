package ch.uzh.ifi.seal.changeadvisor.parser;

import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Created by alex on 20.07.2017.
 */
public class PosBasedFilterTest {

    private PosBasedFilter posBasedFilter = new PosBasedFilter();

    @Test
    public void filter() throws Exception {
        final List<String> nounsAndVerbs = ImmutableList.of(
                "Add", "text", "can", "contain", "sentences", "'s", "pushing", "edge", "'ve", "seen", "was", "thing", "had", "seen", "do");

        List<String> filtered = posBasedFilter.filter("Add your text here! It can contain multiple sentences. It's pushing me over the longest edge I've ever seen it was the evilest thing I had ever seen him do!");

        Assert.assertThat(nounsAndVerbs.size(), is(filtered.size()));
        for (String token : filtered) {
            Assert.assertTrue(nounsAndVerbs.contains(token));
        }
    }

    @Test
    public void filterEmpty() throws Exception {
        List<String> filter = posBasedFilter.filter(null);
        Assert.assertTrue(filter.isEmpty());

        filter = posBasedFilter.filter("");
        Assert.assertTrue(filter.isEmpty());
    }
}