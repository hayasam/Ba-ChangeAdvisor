package ch.uzh.ifi.seal.changeadvisor.ml.math;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.core.Is.is;

public class MatrixTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void init() throws Exception {
        final int rows = 3;
        final int cols = 4;
        Matrix<Integer> m = new Matrix<>(rows, cols, 0);

        int i = 0;
        int j = 0;
        for (List<Integer> row : m) {
            i++;
            for (Integer val : row) {
                j++;
                Assert.assertThat(val, is(0));
            }
            Assert.assertThat(j, is(cols));
            j = 0;
        }
        Assert.assertThat(i, is(rows));

        Assert.assertThat(m.getM(), is(cols));
        Assert.assertThat(m.getN(), is(rows));
    }

    @Test
    public void get() throws Exception {
        final int rows = 3;
        final int cols = 4;
        final int defaultValue = 10;
        Matrix<Integer> m = new Matrix<>(rows, cols, 10);

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Assert.assertThat(m.get(i, j), is(defaultValue));
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void getShouldThrow() throws Exception {
        final int rows = 3;
        final int cols = 4;
        Matrix<Integer> m = new Matrix<>(rows, cols, 10);
        m.get(10, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getShouldThrowNegative() throws Exception {
        final int rows = 3;
        final int cols = 4;
        Matrix<Integer> m = new Matrix<>(rows, cols, 10);
        m.get(-1, -1);
    }

    @Test
    public void set() throws Exception {
        final int rows = 3;
        final int cols = 4;
        Matrix<Integer> m = new Matrix<>(rows, cols, 0);
        m.set(0, 0, 10);
        m.set(1, 1, 20);
        m.set(2, 2, 30);
        m.set(2, 3, 40);

        Assert.assertThat(m.get(0, 0), is(10));
        Assert.assertThat(m.get(1, 1), is(20));
        Assert.assertThat(m.get(2, 2), is(30));
        Assert.assertThat(m.get(2, 3), is(40));
    }

    @Test(expected = IllegalArgumentException.class)
    public void setShouldThrow() throws Exception {
        final int rows = 3;
        final int cols = 4;
        Matrix<Integer> m = new Matrix<>(rows, cols, 0);
        m.set(3, 4, 10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void setShouldThrowNegative() throws Exception {
        final int rows = 3;
        final int cols = 4;
        Matrix<Integer> m = new Matrix<>(rows, cols, 0);
        m.set(-3, -4, 10);
    }
}