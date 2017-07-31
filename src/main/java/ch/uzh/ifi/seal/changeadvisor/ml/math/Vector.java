package ch.uzh.ifi.seal.changeadvisor.ml.math;

import cc.mallet.util.Maths;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class Vector<T extends Number> implements Iterable<T> {

    public static final Vector<Integer> ZEROS_3_D = intZeros(3);

    public static final Vector<Double> ZEROS_3_F = doubleZeros(3);

    public static final Vector<Double> ONES_3_F = new Vector<>(3, 1d);

    private List<T> v;

    private final int n;

    public Vector(int n, T defaultValue) {
        if (n < 0) {
            throw new IllegalArgumentException(String.format("Illegal Capacity: %d", n));
        }
        this.n = n;
        this.v = new ArrayList<>(n);
        init(defaultValue);
    }

    public Vector(List<T> v) {
        assert v != null;
        this.n = v.size();
        this.v = new ArrayList<>(v);
    }

    public Vector(T... values) {
        assert values != null && values.length > 0;

        this.n = values.length;
        this.v = new ArrayList<T>(n);
        v.addAll(Arrays.asList(values));
    }

    private void init(T defaultValue) {
        for (int i = 0; i < n; i++) {
            v.add(defaultValue);
        }
    }

    public List<T> get() {
        return new ArrayList<>(v);
    }

    public T get(int i) {
        assertDimension(i);
        return v.get(i);
    }

    public Vector<T> get(Vector<Integer> ids) {
        return get(ids.v);
    }

    public Vector<T> get(List<Integer> ids) {
        return new Vector<>(ids.stream().map(v::get).collect(Collectors.toList()));
    }

    public void set(int i, T value) {
        assertDimension(i);
        v.set(i, value);
    }

    public <S extends Number> Double dot(Vector<S> other) {
        assertSameDimensions(other);
        double result = 0;
        for (int i = 0; i < n; i++) {
            result += v.get(i).doubleValue() * other.v.get(i).doubleValue();
        }
        return result;
    }

    public Vector<Double> plus(Vector<T> other) {
        assertSameDimensions(other);
        List<Double> vResult = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vResult.add(v.get(i).doubleValue() + other.v.get(i).doubleValue());
        }

        return new Vector<>(vResult);
    }

    public Vector<Double> plus(double val) {
        List<Double> result = v.stream().map(i -> i.doubleValue() + val).collect(Collectors.toList());
        return new Vector<>(result);
    }

    public Vector<Double> minus(Vector<T> other) {
        assertSameDimensions(other);
        List<Double> vResult = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            vResult.add(v.get(i).doubleValue() - other.v.get(i).doubleValue());
        }

        return new Vector<>(vResult);
    }

    public <S extends Number> Vector<Double> times(Vector<S> other) {
        assertSameDimensions(other);
        List<Double> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(v.get(i).doubleValue() * other.v.get(i).doubleValue());
        }
        return new Vector<>(result);
    }

    public <S extends Number> Vector<Double> times(S mult) {
        List<Double> result = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            result.add(v.get(i).doubleValue() * mult.doubleValue());
        }
        return new Vector<>(result);
    }

    public <S extends Number> Vector<Double> dividedBy(S div) {
        return times(1d / div.doubleValue());
    }

    public Vector<Double> logGamma() {
        List<Double> result = v.stream().map(x -> Maths.logGamma(x.doubleValue())).collect(Collectors.toList());
        return new Vector<>(result);
    }

    public Vector<Double> log() {
        List<Double> result = v.stream().map(x -> Math.log(x.doubleValue())).collect(Collectors.toList());
        return new Vector<>(result);
    }

    public Vector<Double> exp() {
        List<Double> result = v.stream().map((T a) -> Math.exp(a.doubleValue())).collect(Collectors.toList());
        return new Vector<>(result);
    }

    public static <T extends Number> Vector<Double> exp(Vector<T> vector) {
        return vector.exp();
    }

    public double sum() {
        return v.stream().mapToDouble(Number::doubleValue).sum();
    }

    public T max() {
        Optional<T> max = v.stream().max(Comparator.comparingDouble(Number::doubleValue));
        return max.orElseThrow(() -> new IllegalArgumentException("Cannot compute max of empty vector!"));
    }

    private void assertDimension(int i) {
        if (i >= n || i < 0) {
            throw new IllegalArgumentException(String.format("Vector dimension mismatch, valid range: (%d); got: (%d", n, i));
        }
    }

    private <S extends Number> void assertSameDimensions(Vector<S> other) {
        if (n != other.size()) {
            throw new IllegalArgumentException(String.format("Vector dimension mismatch: this(%d), other(%d)", n, other.size()));
        }
    }

    public int size() {
        assert n == v.size();
        return n;
    }

    public List<T> asList() {
        return new ArrayList<>(v);
    }

    @Override
    public String toString() {
        return String.format("{%d}: %s", n, v.toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector<?> vector = (Vector<?>) o;

        if (n != vector.n) return false;
        return v != null ? v.equals(vector.v) : vector.v == null;
    }

    @Override
    public int hashCode() {
        int result = v != null ? v.hashCode() : 0;
        result = 31 * result + n;
        return result;
    }

    @NotNull
    @Override
    public Iterator<T> iterator() {
        return v.iterator();
    }

    public static Vector<Integer> intZeros(int n) {
        return new Vector<>(n, 0);
    }

    public static Vector<Double> doubleZeros(int n) {
        return new Vector<>(n, 0d);
    }

    public Vector<T> copy() {
        return new Vector<>(v);
    }

    public static Vector<Double> toDoubleVector(List<? extends Number> list) {
        return new Vector<>(list.stream().map(Number::doubleValue).collect(Collectors.toList()));
    }
}
