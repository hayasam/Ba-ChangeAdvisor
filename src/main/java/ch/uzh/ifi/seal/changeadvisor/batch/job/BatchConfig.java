package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.parser.BagOfWords;
import ch.uzh.ifi.seal.changeadvisor.parser.FSProjectParser;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.ClassBean;
import com.mongodb.MongoClient;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.data.MongoItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

/**
 * Created by alex on 15.07.2017.
 */
@Configuration
@EnableBatchProcessing
public class BatchConfig {

    private static final String TEST_DIRECTORY = "test_files_parser";
    private static final String FROSTWIRE_DIRECTORY = "/com.frostwire.android";

    public final JobBuilderFactory jobBuilderFactory;

    public final StepBuilderFactory stepBuilderFactory;

    private final FSProjectParser projectParser;

    @Autowired
    public BatchConfig(FSProjectParser projectParser, JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.projectParser = projectParser;
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job job(JobCompletionNotificationListener listener) {
        return jobBuilderFactory.get("importUserJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(extractBagOfWords())
                .end()
                .build();
    }

    @Bean
    public Step extractBagOfWords() {
        return stepBuilderFactory.get("extractBagOfWords")
                .<ClassBean, BagOfWords>chunk(10)
                .reader(reader())
                .processor(processor())
//                .writer(writer())
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
    public BagOfWordsProcessor processor() {
        return new BagOfWordsProcessor();
    }

    @Bean
    public FlatFileItemWriter<BagOfWords> writer() {
        FlatFileItemWriter<BagOfWords> writer = new FlatFileItemWriter<>();
        writer.setResource(new FileSystemResource(TEST_DIRECTORY + "/batch_test.csv"));
        writer.setHeaderCallback((headerWriter) -> headerWriter.write("component,bag"));
        writer.setLineAggregator(BagOfWords::asCsv);
        return writer;
    }

    @Bean
    public ItemWriter<BagOfWords> mongoWriter() {
        MongoItemWriter<BagOfWords> mongoItemWriter = new MongoItemWriter<>();
        mongoItemWriter.setTemplate(mongoTemplate());
        return mongoItemWriter;
    }

    @Bean
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(new MongoClient(), "bagOfWords");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory());
    }

}
