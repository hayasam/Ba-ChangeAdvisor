package ch.uzh.ifi.seal.changeadvisor.web.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Corpus {

    private List<Document> documents;

    public Corpus(Collection<Document> documents) {
        this.documents = new ArrayList<>(documents);
    }

    public int size() {
        return documents.size();
    }

    public int documentFrequency(final AbstractNGram token) {
        if (documents.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Document d : documents) {
            if (d.contains(token)) {
                count += 1;
            }
        }
        return count;
    }
}
