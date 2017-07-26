package ch.uzh.ifi.seal.changeadvisor.ml.math;

import org.junit.Assert;

import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;

public class Multinomial {

    private Random generator;

    private double[] distribution;

    private int distributionSize;

    public Multinomial(double[] probabilities) {
        assertProbabilitiesAreOne(probabilities);
        generator = new Random();

        distributionSize = probabilities.length;
        distribution = new double[distributionSize];

        double position = 0;
        for (int i = 0; i < distributionSize; ++i) {
            position += probabilities[i];
            distribution[i] = position;
        }

        distribution[distributionSize - 1] = 1.0;

    }

    public Multinomial(List<Double> probabilities) {
        assertProbabilitiesAreOne(probabilities);
        generator = new Random();

        distributionSize = probabilities.size();
        distribution = new double[distributionSize];

        double position = 0;
        for (int i = 0; i < distributionSize; ++i) {
            position += probabilities.get(i);
            distribution[i] = position;
        }

        distribution[distributionSize - 1] = 1.0;

    }

    private void assertProbabilitiesAreOne(double[] probabilities) {
        double sum = DoubleStream.of(probabilities).sum();
        Assert.assertEquals(sum, 1.0, 0.01);
    }

    private void assertProbabilitiesAreOne(List<Double> probabilities) {
        Double sum = probabilities.stream().reduce((d1, d2) -> d1 + d2).orElse(0.0);
        Assert.assertEquals(sum, 1.0, 0.01);
    }

    public int sample() {
        double uniform = generator.nextDouble();
        for (int i = 0; i < distributionSize; ++i) {
            if (uniform < distribution[i]) {
                return i;
            }
        }
        return distributionSize - 1;
    }

}