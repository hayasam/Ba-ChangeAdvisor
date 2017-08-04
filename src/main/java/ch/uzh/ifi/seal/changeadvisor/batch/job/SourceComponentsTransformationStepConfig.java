package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.bagofwords.FSProjectReader;
import ch.uzh.ifi.seal.changeadvisor.batch.job.bagofwords.SourceCodeProcessor;
import ch.uzh.ifi.seal.changeadvisor.parser.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.parser.FSProjectParser;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.ClassBean;
import ch.uzh.ifi.seal.changeadvisor.parser.preprocessing.*;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import java.util.function.Predicate;

/**
 * Configuration of "Extract Bag of words" step of ChangeAdvisor.
 * Created by alex on 17.07.2017.
 */
@Component
public class SourceComponentsTransformationStepConfig {

    private static final String STEP_NAME = "sourceCodeTransformation";

    private static final String TEST_DIRECTORY = "test_files_parser";

    private static final String FROSTWIRE_DIRECTORY = "/com.frostwire.android";

    private final StepBuilderFactory stepBuilderFactory;

    private final FSProjectParser projectParser;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public SourceComponentsTransformationStepConfig(StepBuilderFactory stepBuilderFactory, FSProjectParser projectParser, MongoTemplate mongoTemplate) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.projectParser = projectParser;
        this.mongoTemplate = mongoTemplate;
    }

    @Bean
    public Step extractBagOfWords() {
        return stepBuilderFactory.get(STEP_NAME)
                .<ClassBean, CodeElement>chunk(10)
                .reader(reader())
                .processor(processor())
                .writer(mongoWriter())
                .build();
    }

    @Bean
    public FSProjectReader reader() {
        FSProjectReader reader = new FSProjectReader(projectParser);
        reader.setProjectRootPath(TEST_DIRECTORY + FROSTWIRE_DIRECTORY);
        reader.setSortedRead(true);
        return reader;
    }

    @Bean
    public SourceCodeProcessor processor() {
        CorpusProcessor processor = new CorpusProcessor.Builder()
                .escapeSpecialChars()
                .withComposedIdentifierSplit(composedIdentifierSplitter())
//                .withAutoCorrect(new EnglishSpellChecker()) // Warning huge performance impact!
                .withContractionExpander(new ContractionsExpander())
                .singularize()
                .removeStopWords()
                .stem()
                .removeTokensShorterThan(3)
                .build();
        return new SourceCodeProcessor(processor);
    }

    @Bean
    public ComposedIdentifierSplitter composedIdentifierSplitter() {
        return new ComposedIdentifierSplitter();
    }

    @Bean
    public EscapeSpecialCharacters escapeSpecialCharacters() {
        return new EscapeSpecialCharacters();
    }

    @Bean
    public SpellChecker spellChecker() {
        return new EnglishSpellChecker();
    }

    @Bean
    public Predicate<String> stopWordFilter() {
        return StopWordFilter::isNotStopWord;
    }

    @Bean
    public FlatFileItemWriter<CodeElement> fileWriter() {
        FlatFileItemWriter<CodeElement> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(TEST_DIRECTORY + "/batch_test.csv"));
        writer.setHeaderCallback(headerWriter -> headerWriter.write("component,bag"));
        writer.setLineAggregator(CodeElement::asCsv);
        return writer;
    }

    @Bean
    public ItemWriter<CodeElement> mongoWriter() {
        MongoItemWriter<CodeElement> mongoItemWriter = new MongoItemWriter<>();
        mongoItemWriter.setTemplate(mongoTemplate);
        return mongoItemWriter;
    }
}
