package ch.uzh.ifi.seal.changeadvisor.web.util;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class TFiDFTest {

    private TFiDF tFiDF = new TFiDF();

    @Test
    public void frequency() throws Exception {
        Document d1 = new Document(Lists.newArrayList("this", "is", "a", "a", "sample"));
        Document d2 = new Document(Lists.newArrayList("this", "is", "another", "another", "example", "example", "example"));
        Document emptyDocument = new Document(Lists.newArrayList());

        double frequency = tFiDF.tf("this", d1);
        Assert.assertThat(frequency, is(1. / 5));

        frequency = tFiDF.tf("this", d2);
        Assert.assertThat(frequency, is(1. / 7));

        frequency = tFiDF.tf("this", emptyDocument);
        Assert.assertThat(frequency, is(0.0));

        frequency = tFiDF.tf("example", d1);
        Assert.assertThat(frequency, is(0.0));

        frequency = tFiDF.tf("example", d2);
        Assert.assertThat(frequency, is(3. / 7));
    }

    @Test
    public void idf() throws Exception {
        Document d1 = new Document(Lists.newArrayList("this", "is", "a", "a", "sample"));
        Document d2 = new Document(Lists.newArrayList("this", "is", "another", "another", "example", "example", "example"));
        Corpus corpus = new Corpus(Lists.newArrayList(d1, d2));

        double result = tFiDF.idf("this", corpus);
        Assert.assertThat(result, is(0.0));

        result = tFiDF.idf("example", corpus);
        Assert.assertThat(result, is(Math.log10(2)));
    }

    @Test
    public void computeTfidf() throws Exception {
        Document d1 = new Document(Lists.newArrayList("this", "is", "a", "a", "sample"));
        Document d2 = new Document(Lists.newArrayList("this", "is", "another", "another", "example", "example", "example"));
        Corpus corpus = new Corpus(Lists.newArrayList(d1, d2));

        double result = tFiDF.computeTfidf("example", d1, corpus);
        Assert.assertThat(result, is(0.0));

        result = tFiDF.computeTfidf("example", d2, corpus);
        Assert.assertThat(result, is(Math.log10(2) * 3. / 7));

        // word not in corpus.
        result = tFiDF.computeTfidf("asdfsdf", d1, corpus);
        Assert.assertThat(result, is(0.0));
        result = tFiDF.computeTfidf("asdfsdf", d2, corpus);
        Assert.assertThat(result, is(0.0));
    }
}