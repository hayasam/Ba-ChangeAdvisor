package ch.uzh.ifi.seal.changeadvisor.ml.math;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Matrix<T> implements Iterable<List<T>> {

    private int n;

    private int m;

    private List<List<T>> table;

    public Matrix(int n, int m, T defaultValue) {
        this.n = n;
        this.m = m;

        this.table = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            table.add(new ArrayList<>(m));
        }

        init(defaultValue);
    }

    public void init(T defaultValue) {
        for (List<T> row : table) {
            for (T val : row) {
                row.set(0, defaultValue);
            }
            for (int i = 0; i < m; i++) {
                if (row.size() >= i) {
                    row.add(defaultValue);
                } else {
                    row.set(i, defaultValue);
                }
            }
        }
    }

    public T get(int n, int m) {
        assertIndexes(n, m);
        return table.get(n).get(m);
    }

    public T set(int n, int m, T value) {
        assertIndexes(n, m);
        return table.get(n).set(m, value);
    }

    private void assertIndexes(int n, int m) {
        if (n < 0 || m < 0 || n >= this.n || m >= this.m) {
            throw new IllegalArgumentException(String.format("Invalid indexes: (%d, %d). Valid range: (%d, %d)", n, m, this.n, this.m));
        }
    }

    public int getN() {
        return n;
    }

    public int getM() {
        return m;
    }

    public int size() {
        return n;
    }

    @NotNull
    @Override
    public Iterator<List<T>> iterator() {
        return table.iterator();
    }

    @Override
    public String toString() {
        return String.format("(%d, %d): %s", n, m, table.toString());
    }
}
