package ch.uzh.ifi.seal.changeadvisor.ml.math;

import cc.mallet.util.Maths;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;

public class VectorTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void init() throws Exception {
        final int n = 3;
        Vector<Integer> v1 = new Vector<>(n, 0);

        List<Integer> list = Lists.newArrayList(1, 2, 3);
        Vector<Integer> v2 = new Vector<>(list);

        Assert.assertThat(v1.size(), is(n));
        Assert.assertThat(v2.size(), is(list.size()));

        for (Integer val : v1) {
            Assert.assertThat(val, is(0));
        }

        for (int i = 0; i < list.size(); i++) {
            Assert.assertThat(list.get(i), is(v2.get(i)));
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void initShouldThrow() throws Exception {
        new Vector<>(-1, 0);
    }

    @Test
    public void dot() throws Exception {
        Vector<Integer> v1 = new Vector<>(-1, 0, 1);
        Vector<Integer> v2 = new Vector<>(1, 0, 1);

        Double dotProduct = v1.dot(v2);
        Assert.assertThat(dotProduct, is(0d));

        v1 = new Vector<>(1, 2, 3);

        dotProduct = v1.dot(v2);
        Assert.assertThat(dotProduct, is(4d));
    }

    @Test(expected = IllegalArgumentException.class)
    public void dotShouldThrow() throws Exception {
        Vector<Integer> v1 = new Vector<>(1);
        Vector<Integer> v2 = new Vector<>(1, 2, 3);
        v1.dot(v2);
    }

    @Test
    public void plus() throws Exception {
        Vector<Integer> v1 = new Vector<>(-1, 0, 1);
        Vector<Integer> v2 = new Vector<>(1, 0, -1);

        Vector<Double> result = v1.plus(v2);
        Vector<Double> expectedResult = new Vector<>(0d, 0d, 0d);
        Assert.assertThat(result, is(expectedResult));

        v1 = new Vector<>(-1, 0, 1);
        v2 = new Vector<>(1, 0, 2);

        result = v1.plus(v2);
        expectedResult = new Vector<>(0d, 0d, 3d);
        Assert.assertThat(result, is(expectedResult));

        v1 = new Vector<>(0, 0, 0);
        v2 = new Vector<>(0, 0, 0);

        result = v1.plus(v2);
        expectedResult = new Vector<>(0d, 0d, 0d);
        Assert.assertThat(result, is(expectedResult));
    }

    @Test
    public void plusComponentWise() throws Exception {
        Vector<Integer> v1 = new Vector<>(-1, 0, 1);
        Vector<Double> result = v1.plus(2);
        Vector<Double> expectedResult = new Vector<>(1d, 2d, 3d);

        Assert.assertThat(result, is(expectedResult));

        v1 = Vector.ZEROS_3_D;
        result = v1.plus(0);
        expectedResult = Vector.doubleZeros(v1.size());

        Assert.assertThat(result, is(expectedResult));

        result = v1.plus(-10);
        expectedResult = new Vector<>(v1.size(), -10d);
        Assert.assertThat(result, is(expectedResult));
    }

    @Test
    public void minus() throws Exception {
        Vector<Integer> v1 = new Vector<>(-1, 0, 1);
        Vector<Integer> v2 = new Vector<>(1, 0, -1);

        Vector<Double> result = v1.minus(v2);
        Vector<Double> expectedResult = new Vector<>(-2d, 0d, 2d);
        Assert.assertThat(result, is(expectedResult));

        v1 = new Vector<>(-1, 0, 1);
        v2 = new Vector<>(1, 0, 2);

        result = v1.minus(v2);
        expectedResult = new Vector<>(-2d, 0d, -1d);
        Assert.assertThat(result, is(expectedResult));

        v1 = new Vector<>(0, 0, 0);
        v2 = new Vector<>(0, 0, 0);

        result = v1.minus(v2);
        expectedResult = new Vector<>(0d, 0d, 0d);
        Assert.assertThat(result, is(expectedResult));
    }

    @Test
    public void times() throws Exception {
        Vector<Integer> v1 = new Vector<>(-1, 0, 1);
        Vector<Integer> v2 = new Vector<>(1, 0, -1);

        Vector<Double> result = v1.times(v2);
        Vector<Double> expectedResult = new Vector<>(-1d, 0d, -1d);
        Assert.assertThat(result, is(expectedResult));

        result = Vector.ZEROS_3_F.times(Vector.ZEROS_3_F);
        Assert.assertThat(result, is(Vector.ZEROS_3_F));
    }

    @Test(expected = IllegalArgumentException.class)
    public void timeShouldThrow() throws Exception {
        new Vector<>(1, 2, 3).times(new Vector<>(1));
    }

    @Test
    public void logGamma() throws Exception {
        Vector<Integer> v = new Vector<>(1, 2, 3);
        Vector<Double> result = v.logGamma();

        for (int i = 0; i < v.size(); i++) {
            Assert.assertThat(Maths.logGamma(v.get(i)), is(result.get(i)));
        }
    }

    @Test
    public void logGammaInfinity() {
        Vector<Double> result = Vector.ZEROS_3_F.logGamma();
        for (Double val : result) {
            Assert.assertTrue(val.isInfinite());
        }
    }

    @Test
    public void logGammaShouldNaN() throws Exception {
        Vector<Double> result = new Vector<>(3, -1d).logGamma();
        for (Double val : result) {
            Assert.assertTrue(val.isNaN());
        }
    }

    @Test
    public void exp() throws Exception {
        Vector<Double> result = Vector.ZEROS_3_F.exp();
        Assert.assertThat(result, is(Vector.ONES_3_F));

        result = Vector.ONES_3_F.exp();
        Assert.assertThat(result, is(new Vector<>(3, Math.exp(1))));


        result = new Vector<>(1, 2, 3).exp();
        Vector<Double> expectedResult = new Vector<>(Math.exp(1), Math.exp(2), Math.exp(3));
        Assert.assertThat(result, is(expectedResult));

    }
}