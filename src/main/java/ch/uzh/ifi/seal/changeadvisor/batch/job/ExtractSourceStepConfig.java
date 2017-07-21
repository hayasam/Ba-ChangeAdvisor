package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.bagofwords.FSProjectReader;
import ch.uzh.ifi.seal.changeadvisor.parser.BagOfWords;
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
public class ExtractSourceStepConfig {

    private static final String STEP_NAME = "extractSource";

    private static final String TEST_DIRECTORY = "test_files_parser";

    private static final String FROSTWIRE_DIRECTORY = "/com.frostwire.android";

    private final StepBuilderFactory stepBuilderFactory;

    private final FSProjectParser projectParser;

    private final MongoTemplate mongoTemplate;

    @Autowired
    public ExtractSourceStepConfig(StepBuilderFactory stepBuilderFactory, FSProjectParser projectParser, MongoTemplate mongoTemplate) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.projectParser = projectParser;
        this.mongoTemplate = mongoTemplate;
    }

    @Bean
    public Step extractBagOfWords() {
        return stepBuilderFactory.get(STEP_NAME)
                .<ClassBean, BagOfWords>chunk(10)
                .reader(reader())
                .processor(preprocessor())
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
    public CorpusProcessor preprocessor() {
        return new CorpusProcessor.Builder(true, tokenizer())
                .step(lowerCaseStep())
                .step(stemmerStep())
                .filter(stopWordFilter())
                .filter(tokenLengthFilter())
                .build();
    }

    @Bean
    public Tokenizer tokenizer() {
        return new ComposedIdentifierSplitter();
    }

    @Bean
    public ProcessingStep lowerCaseStep() {
        return new LowerCaseProcessor();
    }

    @Bean
    public ProcessingStep stemmerStep() {
        return new Stemmer(Stemmer.MINIMUM_WORD_LENGTH);
    }

    @Bean
    public Predicate<String> stopWordFilter() {
        return StopWordFilter::isNotStopWord;
    }

    @Bean
    public Predicate<String> tokenLengthFilter() {
        return t -> t.length() > 3;
    }


    @Bean
    public FlatFileItemWriter<BagOfWords> fileWriter() {
        FlatFileItemWriter<BagOfWords> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(TEST_DIRECTORY + "/batch_test.csv"));
        writer.setHeaderCallback((headerWriter) -> headerWriter.write("component,bag"));
        writer.setLineAggregator(BagOfWords::asCsv);
        return writer;
    }

    @Bean
    public ItemWriter<BagOfWords> mongoWriter() {
        MongoItemWriter<BagOfWords> mongoItemWriter = new MongoItemWriter<>();
        mongoItemWriter.setTemplate(mongoTemplate);
        return mongoItemWriter;
    }
}
