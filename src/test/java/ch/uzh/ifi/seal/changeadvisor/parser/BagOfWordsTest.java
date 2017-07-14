package ch.uzh.ifi.seal.changeadvisor.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.google.common.base.Splitter;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alex on 14.07.2017.
 */
public class BagOfWordsTest {

    private static final String TEST_DIRECTORY = "test_files_parser";
    private static final String TEST_FROSTWIRE_APP = "com.frostwire.android";

    @Test
    public void frostwireClass() throws Exception {
        String bagOfWordFromPoC = "convert activ void call name inherit cach click final holder build paus user extra start grid text constructor listen touch play privat disk util otherwis para fetcher stabl view gener null music adapt list label unload layout datum line context item imag true count background overlay link boolean temporarili flush album resourc plural artist style load overrid type return public posit method popul fals parent song artwork code hold color idea els remov super pollo determin inflat";
        List<String> split = new ArrayList<>(Splitter.on(' ').trimResults().omitEmptyStrings().splitToList(bagOfWordFromPoC));
        Collections.sort(split);

        Path path = Paths.get(TEST_DIRECTORY + "/" + TEST_FROSTWIRE_APP + "/android/apollo/src/com/andrew/apollo/adapters/AlbumAdapter.java");
        CompilationUnit cu = JavaParser.parse(path);
        String packageName = cu.getPackageDeclaration().get().getName().toString();

        String classString = FileUtils.readFileToString(path.toFile(), "utf-8");
        BagOfWords bagOfWords = BagOfWords.fromCorpus(packageName, classString);

        bagOfWords.writeToFile(Paths.get(TEST_DIRECTORY + "/test_generated/processed_source_components.csv"), false);

        List<String> orderedBagOfWords = bagOfWords.getOrderedBagOfWords();

        Assert.assertEquals(split.size(), orderedBagOfWords.size());
    }
}