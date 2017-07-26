package ch.uzh.ifi.seal.changeadvisor.ml;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Corpus implements Iterable<Set<String>> {

    private List<Set<String>> documents;

    public Corpus(List<Set<String>> documents) {
        this.documents = documents;
    }

    public void addDocument(Set<String> tokens) {
        documents.add(tokens);
    }

    public List<Set<String>> getDocuments() {
        return documents;
    }

    @Override
    public String toString() {
        return "Corpus{" +
                "documents=" + documents +
                '}';
    }

    @NotNull
    @Override
    public Iterator<Set<String>> iterator() {
        return documents.iterator();
    }
}
