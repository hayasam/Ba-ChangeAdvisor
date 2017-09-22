package ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArdocResultRepository extends MongoRepository<ArdocResult, String> {

    List<ArdocResult> findByAppName(String appName);

    List<ArdocResult> findByAppNameOrderByReview_ReviewDateDesc(String appName);

    Optional<ArdocResult> findFirstByAppNameOrderByReview_ReviewDateDesc(String appName);
}
