package ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelRepository extends MongoRepository<Label, String> {

    void deleteAllByAppName(String appName);

    List<Label> findByAppNameAndNgramSizeOrderByScoreDesc(String appName, Integer ngramSize);

    List<Label> findByAppNameAndCategoryAndNgramSizeOrderByScoreDesc(String appName, String category, Integer ngramSize);
}
