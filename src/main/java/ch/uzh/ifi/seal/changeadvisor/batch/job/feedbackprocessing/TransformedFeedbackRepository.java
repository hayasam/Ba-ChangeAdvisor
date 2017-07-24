package ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by alex on 24.07.2017.
 */
@Repository
public interface TransformedFeedbackRepository extends MongoRepository<TransformedFeedback, String> {
}
