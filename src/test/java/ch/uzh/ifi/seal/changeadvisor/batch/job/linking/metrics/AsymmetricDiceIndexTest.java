package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.core.Is.is;

public class AsymmetricDiceIndexTest {

    private final AsymmetricDiceIndex asymmetricDiceIndex = new AsymmetricDiceIndex();

    @Test
    public void similarity() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        Set<String> doc2 = Sets.newHashSet("foo", "bar", "zzz", "www", "world");
        Set<String> doc3 = Sets.newHashSet("aa", "bb", "zzz", "www", "cc");
        Set<String> doc4 = Sets.newHashSet("aa", "bb", "zzz", "hello", "cc");
        Set<String> doc5 = IntStream.range(0, 100).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toSet());
        doc5.add("foo");


        double similarity = asymmetricDiceIndex.similarity(doc1, doc2);

        Assert.assertThat(similarity, is(1.0));

        similarity = asymmetricDiceIndex.similarity(doc2, doc3);
        Assert.assertThat(similarity, is(0.8));

        similarity = asymmetricDiceIndex.similarity(doc2, doc4);
        Assert.assertThat(similarity, is(0.4));

        similarity = asymmetricDiceIndex.similarity(doc1, doc5);
        Assert.assertThat(similarity, is(0.4));
    }

    @Test
    public void noSimilarity() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        Set<String> doc2 = Sets.newHashSet("aa", "bb", "zzz", "www", "cc");
        Set<String> doc3 = IntStream.range(0, 99).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toSet());
        Set<String> doc4 = IntStream.range(0, 99).mapToObj(i -> UUID.randomUUID().toString()).collect(Collectors.toSet());


        double similarity = asymmetricDiceIndex.similarity(doc1, doc2);
        Assert.assertThat(similarity, is(0.0));

        similarity = asymmetricDiceIndex.similarity(doc1, doc4);
        Assert.assertThat(similarity, is(0.0));

        similarity = asymmetricDiceIndex.similarity(doc3, doc4);
        Assert.assertThat(similarity, is(0.0));
    }
}
