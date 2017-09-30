package ch.uzh.ifi.seal.changeadvisor.web.util;

import com.google.common.collect.ImmutableList;
import org.springframework.util.Assert;

import java.util.List;

public class NGram extends AbstractNGram<List<String>> {

    private final List<String> tokens;

    public NGram(List<String> tokens) {
        Assert.notNull(tokens, "Tokens cannot be null!");
        this.tokens = ImmutableList.copyOf(tokens);
    }

    @Override
    public List<String> getTokens() {
        return tokens;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NGram nGram = (NGram) o;

        return tokens.equals(nGram.tokens);
    }

    @Override
    public int hashCode() {
        return tokens.hashCode();
    }
}
