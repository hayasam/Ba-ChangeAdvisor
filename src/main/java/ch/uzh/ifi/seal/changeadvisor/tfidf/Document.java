package ch.uzh.ifi.seal.changeadvisor.tfidf;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Document {

    private List<AbstractNGram> document;

    public Document(List<AbstractNGram> document) {
        this.document = document;
    }

    public int size() {
        return document.size();
    }

    public double frequency(AbstractNGram token) {
        if (document.isEmpty()) {
            return 0.0;
        }

        double count = 0;
        for (AbstractNGram word : document) {
            if (word.equals(token)) {
                count += 1;
            }
        }
        return count / size();
    }

    public boolean contains(AbstractNGram token) {
        return document.contains(token);
    }

    public List<AbstractNGram> tokens() {
        return document;
    }

    public List<AbstractNGram> uniqueTokens() {
        return new ArrayList<>(new HashSet<>(document));
    }
}
