package ch.uzh.ifi.seal.changeadvisor.parser;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by alex on 14.07.2017.
 */
public class BagOfWordsExtractorTest {

    private static final String TEST_DIRECTORY = "test_files_parser";
    private static final String TEST_FROSTWIRE_APP = "com.frostwire.android";

    private Path frostwirePath = Paths.get(TEST_DIRECTORY + "/" + TEST_FROSTWIRE_APP);
    private Path exportPath = Paths.get(TEST_DIRECTORY + "/test_generated/processed_source_components.csv");

    @Test
    public void extractFromFrostwire() throws Exception {

        new BagOfWordsExtractor().extractBagOfWordsToFile(frostwirePath, exportPath);
    }
}