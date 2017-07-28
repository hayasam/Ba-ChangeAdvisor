package ch.uzh.ifi.seal.changeadvisor.ml.math;

import java.util.Collection;

public class MockMultinomial implements Multinomial {

    @Override
    public void init(Collection<Double> probabilities) {
    }

    @Override
    public int sample() {
        return 0;
    }
}
