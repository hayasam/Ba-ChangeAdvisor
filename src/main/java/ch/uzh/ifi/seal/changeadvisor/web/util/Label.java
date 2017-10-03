package ch.uzh.ifi.seal.changeadvisor.web.util;

import org.jetbrains.annotations.NotNull;

public class Label<T> implements Comparable<Label> {

    private AbstractNGram<T> token;

    private double score;

    public Label(AbstractNGram<T> token, double score) {
        this.token = token;
        this.score = score;
    }

    public T getToken() {
        return token.getTokens();
    }

    public double getScore() {
        return score;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Label that = (Label) o;

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
    public int compareTo(@NotNull Label o) {
        return Double.compare(score, o.score);
    }

    @Override
    public String toString() {
        return "Label{" +
                "token='" + token.toString() + '\'' +
                ", score=" + score +
                '}';
    }
}
