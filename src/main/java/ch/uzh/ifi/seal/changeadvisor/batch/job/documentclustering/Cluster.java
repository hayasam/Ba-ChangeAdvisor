package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkableReview;

import java.util.Collection;
import java.util.UUID;

public class Cluster {

    private String topicId;

    private Collection<? extends LinkableReview> assignments;

    public Cluster(Collection<? extends LinkableReview> assignments) {
        this.topicId = UUID.randomUUID().toString();
        this.assignments = assignments;
    }

    public Cluster(String topicId, Collection<? extends LinkableReview> assignments) {
        this.topicId = topicId;
        this.assignments = assignments;
    }

    public String getTopicId() {
        return topicId;
    }

    public Collection<? extends LinkableReview> getAssignments() {
        return assignments;
    }

    @Override
    public String toString() {
        return "Cluster{" +
                "topicId=" + topicId +
                ", assignments=" + assignments +
                '}';
    }
}
