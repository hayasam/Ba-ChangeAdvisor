package ch.uzh.ifi.seal.changeadvisor.web.util;

public class TFiDF<T> {

    public double computeTfidf(T token, Document<T> document, Corpus<T> documents) {
        return tf(token, document) * idf(token, documents);
    }

    double tf(T token, Document<T> document) {
        return document.frequency(token);
    }

    double idf(T token, Corpus<T> documents) {
        int documentFrequency = documents.documentFrequency(token);
        if (documentFrequency == 0) {
            return 0.0;
        }
        return Math.log10(documents.size() / documentFrequency);
    }
}
