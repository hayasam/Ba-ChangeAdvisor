package ch.uzh.ifi.seal.changeadvisor.ml;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;

public class Corpus implements Iterable<List<String>> {

    private List<List<String>> documents;

    public Corpus(List<List<String>> documents) {
        this.documents = documents;
    }

    public void addDocument(List<String> tokens) {
        documents.add(tokens);
    }

    public List<List<String>> getDocuments() {
        return documents;
    }

    public int size() {
        return documents.size();
    }

    @Override
    public String toString() {
        return "Corpus{" +
                "documents=" + documents +
                '}';
    }

    @NotNull
    @Override
    public Iterator<List<String>> iterator() {
        return documents.iterator();
    }
}
