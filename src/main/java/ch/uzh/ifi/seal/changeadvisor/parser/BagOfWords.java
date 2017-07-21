package ch.uzh.ifi.seal.changeadvisor.parser;

import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.PreprocessingFacade;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * Represents a Bag-of-Words. A set of words essentially.
 * Created by alex on 14.07.2017.
 */
public class BagOfWords implements Comparable<BagOfWords> {

    private String fullyQualifiedClassName;

    private Set<String> bag;

    private LocalDateTime timestamp = LocalDateTime.now();

    public BagOfWords(String fullyQualifiedClassName, Set<String> bag) {
        this.fullyQualifiedClassName = fullyQualifiedClassName;
        this.bag = bag;
    }

    /**
     * Factory method to instantiate a new BagOfWords.
     *
     * @param fullyQualifiedClassName the fqcn of the class this bag comes from.
     * @param corpus                  text content. The future bagOfWords.
     * @return a bagOfWords containing the processed corpus and the fullyQualifiedClassName.
     */
    public static BagOfWords fromCorpus(final String fullyQualifiedClassName, final String corpus) {
        final Set<String> preprocessed = PreprocessingFacade.preprocess(corpus);
        return new BagOfWords(fullyQualifiedClassName, ImmutableSet.copyOf(preprocessed));
    }

    public Set<String> getBag() {
        return bag;
    }

    /**
     * Returns an immutable sorted copy of this bag.
     *
     * @return immutable sorted bag of words.
     */
    public List<String> getOrderedBagOfWords() {
        return ImmutableList.sortedCopyOf(bag);
    }

    public String getFullyQualifiedClassName() {
        return fullyQualifiedClassName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int size() {
        return bag.size();
    }

    /**
     * Writes bag of words to file as .csv with the following format:
     * packageName,bagOfWords
     *
     * @param path   path to the file to write
     * @param append if {@code true}, then the data will be added to the
     *               end of the file rather than overwriting
     * @throws IOException in case of an I/O error
     */
    public void writeToFile(Path path, boolean append) throws IOException {
        final String bagString = asCsv();
        FileUtils.write(path.toFile(), fullyQualifiedClassName + "," + bagString, "utf8", append);
    }

    public String asCsv() {
        String bagString = String.join(" ", bag);
        return String.format("%s,%s", fullyQualifiedClassName, bagString);
    }

    @Override
    public String toString() {
        return bag.toString();
    }

    /**
     * Compares two bags.
     * Note: this class has a natural ordering that is inconsistent with equals.
     *
     * @param o other bag.
     * @return lexicographical comparison of FQCN names.
     */
    @Override
    public int compareTo(BagOfWords o) {
        return fullyQualifiedClassName.compareTo(o.fullyQualifiedClassName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BagOfWords that = (BagOfWords) o;

        if (fullyQualifiedClassName != null ? !fullyQualifiedClassName.equals(that.fullyQualifiedClassName) : that.fullyQualifiedClassName != null)
            return false;
        if (bag != null ? !bag.equals(that.bag) : that.bag != null) return false;
        return timestamp != null ? timestamp.equals(that.timestamp) : that.timestamp == null;
    }

    @Override
    public int hashCode() {
        int result = fullyQualifiedClassName != null ? fullyQualifiedClassName.hashCode() : 0;
        result = 31 * result + (bag != null ? bag.hashCode() : 0);
        result = 31 * result + (timestamp != null ? timestamp.hashCode() : 0);
        return result;
    }
}
