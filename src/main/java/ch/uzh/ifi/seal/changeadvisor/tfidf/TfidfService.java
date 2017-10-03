package ch.uzh.ifi.seal.changeadvisor.tfidf;

import ch.uzh.ifi.seal.changeadvisor.web.util.*;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TfidfService {

    private TFiDF tFiDF = new TFiDF();

    public List<TfidfToken> computeTfidfScoreForTokens(List<? extends AbstractNGram> tokens, Document document, Corpus corpus) {
        return tokens.stream()
                .map(token -> new TfidfToken(token, tFiDF.compute(token, document, corpus)))
                .collect(Collectors.toList());
    }

    public <T> TfidfToken<T> computeTfidfScoreForToken(AbstractNGram<T> token, Document document, Corpus corpus) {
        TfidfToken<T> tokenWithScore = new TfidfToken<>(token, tFiDF.compute(token, document, corpus));
        return tokenWithScore;
    }

}
