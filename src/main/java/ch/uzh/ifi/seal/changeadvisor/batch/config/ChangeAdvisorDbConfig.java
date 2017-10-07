package ch.uzh.ifi.seal.changeadvisor.batch.config;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.LabelRepository;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectoryRepository;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * Configurations to connect to local mongodb instance.
 * Created by alex on 17.07.2017.
 */
@Configuration
@EnableMongoRepositories(basePackageClasses = {CodeElementRepository.class, SourceCodeDirectoryRepository.class, TopicRepository.class, TransformedFeedbackRepository.class, ArdocResultRepository.class, LinkingResultRepository.class, LabelRepository.class}, mongoTemplateRef = "mongoOperations")
public class ChangeAdvisorDbConfig extends AbstractRepoConfig {

    @Bean
    @Primary
    public MongoProperties primaryDataSource() {
        return new MongoProperties();
    }

    @Override
    protected String getDatabaseName() {
        final MongoClientURI mongoClientURI = new MongoClientURI(primaryDataSource().getUri());
        return mongoClientURI.getDatabase();
    }

    @Override
    protected MongoClientURI getPropertiesMongoUri() {
        return new MongoClientURI(primaryDataSource().getUri());
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(primaryDataSource().getHost(), primaryDataSource().getPort());
    }

    @Bean(name = {"mongoOperations"})
    @Override
    public MongoTemplate mongoTemplate() throws Exception {
        return new MongoTemplate(mongo(), getDatabaseName());
    }
}
