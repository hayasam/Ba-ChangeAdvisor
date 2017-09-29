package ch.uzh.ifi.seal.changeadvisor.web.util;

import org.jetbrains.annotations.NotNull;

public class TfidfToken<T> implements Comparable<TfidfToken<T>> {

    private T token;

    private double score;

    public TfidfToken(T token, double score) {
        this.token = token;
        this.score = score;
    }

    public T getToken() {
        return token;
    }

    public double getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TfidfToken<?> that = (TfidfToken<?>) o;

        if (Double.compare(that.score, score) != 0) return false;
        return token != null ? token.equals(that.token) : that.token == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = token != null ? token.hashCode() : 0;
        temp = Double.doubleToLongBits(score);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public int compareTo(@NotNull TfidfToken o) {
        return Double.compare(score, o.score);
    }

    @Override
    public String toString() {
        return "TfidfToken{" +
                "token='" + token.toString() + '\'' +
                ", score=" + score +
                '}';
    }
}
