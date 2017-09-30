package ch.uzh.ifi.seal.changeadvisor.web.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Document {

    private List<? extends AbstractNGram> document;

    public Document(List<? extends AbstractNGram> document) {
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

    public List<? extends AbstractNGram> tokens() {
        return document;
    }

    public List<? extends AbstractNGram> uniqueTokens() {
        return new ArrayList<>(new HashSet<>(document));
    }
}
