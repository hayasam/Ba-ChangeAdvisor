package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import java.util.Collection;

public class Cluster {

    private int topicId;

    private Collection<TopicAssignment> assignments;

    public Cluster(int topicId, Collection<TopicAssignment> assignments) {
        this.topicId = topicId;
        this.assignments = assignments;
    }

    public int getTopicId() {
        return topicId;
    }

    public Collection<TopicAssignment> getAssignments() {
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
