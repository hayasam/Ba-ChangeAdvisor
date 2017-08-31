package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ArdocResultRepository extends MongoRepository<ArdocResult, String> {
}
