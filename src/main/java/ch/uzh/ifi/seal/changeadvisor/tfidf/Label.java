package ch.uzh.ifi.seal.changeadvisor.tfidf;

import org.jetbrains.annotations.NotNull;

public class Label implements Comparable<Label> {

    private String token;

    private double score;

    public Label(AbstractNGram token, double score) {
        this.token = token.toString();
        this.score = score;
    }

    public String getLabel() {
        return token;
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
                "token='" + token + '\'' +
                ", score=" + score +
                '}';
    }
}
