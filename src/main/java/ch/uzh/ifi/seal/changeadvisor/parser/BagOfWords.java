package ch.uzh.ifi.seal.changeadvisor.parser;

import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.PreprocessingFacade;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * Represents a Bag-of-Words. A set of words essentially.
 * Created by alex on 14.07.2017.
 */
public class BagOfWords {

    private String packageName;

    private Set<String> bagOfWords;

    public BagOfWords(String packageName, Set<String> bagOfWords) {
        this.packageName = packageName;
        this.bagOfWords = bagOfWords;
    }

    /**
     * Factory method to instantiate a new BagOfWords.
     *
     * @param packageName the name of the package this bag comes from.
     * @param corpus      text content. The future bagOfWords.
     * @return a bagOfWords containing the processed corpus and the packageName.
     */
    public static BagOfWords fromCorpus(final String packageName, final String corpus) {
        final Set<String> preprocessed = PreprocessingFacade.preprocess(corpus);
        return new BagOfWords(packageName, ImmutableSet.copyOf(preprocessed));
    }

    public Set<String> getBagOfWords() {
        return bagOfWords;
    }

    /**
     * Returns an immutable sorted copy of this bag.
     *
     * @return immutable sorted bag of words.
     */
    public List<String> getOrderedBagOfWords() {
        return ImmutableList.sortedCopyOf(bagOfWords);
    }

    public int size() {
        return bagOfWords.size();
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
        String bagString = bagOfWords.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(", ", " ");
        FileUtils.write(path.toFile(), packageName + "," + bagString, "utf8", append);
    }

    @Override
    public String toString() {
        return bagOfWords.toString();
    }
}
