package ch.uzh.ifi.seal.changeadvisor.ml.math;

import java.util.List;

public class MockMultinomial implements Multinomial {

    public MockMultinomial(List<Double> probabilities) {
    }

    @Override
    public int sample() {
        return 0;
    }
}
