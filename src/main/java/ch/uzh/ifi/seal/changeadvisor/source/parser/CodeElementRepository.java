package ch.uzh.ifi.seal.changeadvisor.source.parser;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Represents a code component. A set of words and the code component they are derived from.
 * Created by alex on 14.07.2017.
 */
@Repository
public interface CodeElementRepository extends MongoRepository<CodeElement, String> {

}
