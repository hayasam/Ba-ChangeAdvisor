package ch.uzh.ifi.seal.changeadvisor.ml.math;

import org.junit.Assert;
import org.junit.Test;

public class MultinomialTest {
    @Test
    public void sample() throws Exception {
        double[] test = new double[]{0.1, 0.2, 0.7};
        Multinomial multinomial = new Multinomial(test);

        for (int i = 0; i < 20; i++) {
            int sample = multinomial.sample();
            System.out.println(sample);
            Assert.assertTrue(sample < test.length);
        }
    }

}