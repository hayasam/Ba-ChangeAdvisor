package ch.uzh.ifi.seal.changeadvisor.web.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Corpus<T> {

    private List<Document<T>> documents;

    public Corpus(Collection<Document<T>> documents) {
        this.documents = new ArrayList<>(documents);
    }

    public int size() {
        return documents.size();
    }

    public int documentFrequency(final T token) {
        if (documents.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Document<T> d : documents) {
            if (d.contains(token)) {
                count += 1;
            }
        }
        return count;
    }
}
