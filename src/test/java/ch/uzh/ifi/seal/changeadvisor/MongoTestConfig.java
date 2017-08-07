package ch.uzh.ifi.seal.changeadvisor;

import com.mongodb.MongoClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

@Configuration
public class MongoTestConfig {

    @Bean
    @Profile("test")
    public MongoDbFactory mongoDbFactory() {
        return new SimpleMongoDbFactory(new MongoClient(), "changeAdvisorTest");
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoDbFactory());
    }
}
