package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Created by alex on 24.07.2017.
 */
public class TopicClusteringResult {

    private List<Topic> topics;

    private Collection<TopicAssignment> assignments;

    public TopicClusteringResult(List<Topic> topics, Collection<TopicAssignment> assignments) {
        this.topics = topics;
        this.assignments = assignments;
    }

    public List<Topic> getTopics() {
        return topics;
    }

    public void setTopics(List<Topic> topics) {
        this.topics = topics;
    }

    public Collection<TopicAssignment> getAssignments() {
        return assignments;
    }

    public void setAssignments(Set<TopicAssignment> assignments) {
        this.assignments = assignments;
    }

    @Override
    public String toString() {
        return "TopicClusteringResult{" +
                "topics=" + topics +
                ", assignments=" + assignments +
                '}';
    }
}
