package ch.uzh.ifi.seal.changeadvisor.web.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Document<T> {

    private List<T> document;

    public Document(List<T> document) {
        this.document = document;
    }

    public int size() {
        return document.size();
    }

    public double frequency(T token) {
        if (document.isEmpty()) {
            return 0.0;
        }

        double count = 0;
        for (T word : document) {
            if (word.equals(token)) {
                count += 1;
            }
        }
        return count / size();
    }

    public boolean contains(T token) {
        return document.contains(token);
    }

    public List<T> tokens() {
        return document;
    }

    public List<T> uniqueTokens() {
        return new ArrayList<>(new HashSet<>(document));
    }
}
