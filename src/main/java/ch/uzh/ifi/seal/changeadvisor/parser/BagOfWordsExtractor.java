package ch.uzh.ifi.seal.changeadvisor.parser;

import ch.uzh.ifi.seal.changeadvisor.parser.bean.ClassBean;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.PackageBean;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Given a project path it extracts bags of words for each class and then writes it to file.
 * Created by alex on 14.07.2017.
 */
public class BagOfWordsExtractor {

    private static final Logger logger = Logger.getLogger(BagOfWordsExtractor.class);

    private FSProjectParser projectParser = new FSProjectParser();

    public void extractBagOfWordsToFile(Path project, Path exportPath) {
        List<PackageBean> packages = projectParser.parse(project);
        List<BagOfWords> bags = new LinkedList<>();
        for (PackageBean packageBean : packages) {
            for (ClassBean classBean : packageBean.getClasses()) {
                bags.add(BagOfWords.fromCorpus(classBean.getFullyQualifiedClassName(), classBean.getPublicCorpus()));
            }
        }
        Collections.sort(bags);

        bags.forEach(b -> {
            try {
                b.writeToFile(exportPath, true);
            } catch (IOException e) {
                logger.error(String.format("Failed to write bagOfWord for Class: %s", b.getFullyQualifiedClassName()), e);
            }
        });
    }
}
