package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

import static org.hamcrest.core.Is.is;

public class AsymmetricDiceIndexTest {

    @Test
    public void countOverlappingWords() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        Set<String> doc2 = Sets.newHashSet("foo", "bar", "zzz", "www", "world");
        Set<String> doc3 = Sets.newHashSet("aa", "bb", "zzz", "www", "cc");

        int i = new AsymmetricDiceIndex().countOverlappingWords(doc1, doc2);
        Assert.assertThat(i, is(3));

        i = new AsymmetricDiceIndex().countOverlappingWords(doc1, doc3);
        Assert.assertThat(i, is(0));
    }

}