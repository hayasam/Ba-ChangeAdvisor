package ch.uzh.ifi.seal.changeadvisor.batch.job.linking.metrics;

import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.core.Is.is;

public class AsymmetricDiceIndexTest {

    @Test
    public void similarity() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        Set<String> doc2 = Sets.newHashSet("foo", "bar", "zzz", "www", "11");

        double similarity = new AsymmetricDiceIndex().similarity(doc1, doc2);
        Assert.assertThat(similarity, is(0.8));
    }

    @Test
    public void similaritySameDocs() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");

        double similarity = new AsymmetricDiceIndex().similarity(doc1, doc1);
        Assert.assertThat(similarity, is(1.0));
    }

    @Test
    public void similarityNoSimilarity() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        Set<String> doc2 = Sets.newHashSet("aa", "bb", "zzz", "www", "cc");

        double similarity = new AsymmetricDiceIndex().similarity(doc1, doc2);
        Assert.assertThat(similarity, is(0.0));
    }

    @Test
    public void countOverlappingWords() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        Set<String> doc2 = Sets.newHashSet("foo", "bar", "zzz", "www", "world");
        Set<String> doc3 = Sets.newHashSet("aa", "bb", "zzz", "www", "cc");

        int i = new AsymmetricDiceIndex().countOverlappingWords(doc1, doc2);
        Assert.assertThat(i, is(3));

        i = new AsymmetricDiceIndex().countOverlappingWords(doc2, doc1);
        Assert.assertThat(i, is(3));

        i = new AsymmetricDiceIndex().countOverlappingWords(doc2, doc3);
        Assert.assertThat(i, is(2));
    }

    @Test
    public void countOverlappingWordsEmptyDocuments() throws Exception {
        int i = new AsymmetricDiceIndex().countOverlappingWords(new HashSet<>(), new HashSet<>());
        Assert.assertThat(i, is(0));
    }

    @Test
    public void countOverlappingWordsEmptyDocument() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "baz", "hello", "world");
        int i = new AsymmetricDiceIndex().countOverlappingWords(new HashSet<>(), doc1);
        Assert.assertThat(i, is(0));

        i = new AsymmetricDiceIndex().countOverlappingWords(doc1, new HashSet<>());
        Assert.assertThat(i, is(0));
    }

    @Test
    public void countOverlappingWordsNotOverlapping() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "zzz", "www", "world");
        Set<String> doc2 = Sets.newHashSet("aa", "bb", "xxx", "sss", "cc");
        int i = new AsymmetricDiceIndex().countOverlappingWords(doc2, doc1);
        Assert.assertThat(i, is(0));

        i = new AsymmetricDiceIndex().countOverlappingWords(doc1, doc2);
        Assert.assertThat(i, is(0));
    }

    @Test
    public void countOverlappingWordsSameDocument() throws Exception {
        Set<String> doc1 = Sets.newHashSet("foo", "bar", "zzz", "www", "world");
        int i = new AsymmetricDiceIndex().countOverlappingWords(doc1, doc1);
        Assert.assertThat(i, is(doc1.size()));
    }
}