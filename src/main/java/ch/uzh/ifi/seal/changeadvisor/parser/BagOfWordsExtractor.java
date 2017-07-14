package ch.uzh.ifi.seal.changeadvisor.parser;

import ch.uzh.ifi.seal.changeadvisor.parser.bean.ClassBean;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.CompilationUnitBean;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.PackageBean;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by alex on 14.07.2017.
 */
public class BagOfWordsExtractor {

    private static final Logger logger = Logger.getLogger(BagOfWordsExtractor.class);

    private FSProjectParser projectParser = new FSProjectParser();

    public void extractBagOfWordsToFile(Path project, Path exportPath) {
        List<PackageBean> packages = projectParser.parse(project);
        List<BagOfWords> bagOfWords = new ArrayList<>();
        for (PackageBean packageBean : packages) {
            for (CompilationUnitBean compilationUnit : packageBean.getCompilationUnits()) {
                for (ClassBean classBean : compilationUnit.getClasses()) {
                    BagOfWords ofWords = BagOfWords.fromCorpus(classBean.getFullyQualifiedClassName(), classBean.getPublicCorpus());
                    bagOfWords.add(ofWords);
                }
            }
        }
        Collections.sort(bagOfWords);

        bagOfWords.forEach(b -> {
            try {
                b.writeToFile(exportPath, true);
            } catch (IOException e) {
                logger.error(String.format("Failed to write bagOfWord for Class: %s", b.getFullyQualifiedClassName()), e);
            }
        });
    }
}
