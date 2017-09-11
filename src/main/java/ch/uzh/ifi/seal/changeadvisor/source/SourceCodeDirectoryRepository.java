package ch.uzh.ifi.seal.changeadvisor.source;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SourceCodeDirectoryRepository extends MongoRepository<SourceCodeDirectory, String> {

    Optional<SourceCodeDirectory> findByProjectName(String projectName);
}
