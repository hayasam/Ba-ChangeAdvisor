package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.Is.is;

public class ChangeAdvisorLinkerTest {

    private ChangeAdvisorLinker linker = new ChangeAdvisorLinker();

    private static final int TOPIC_SIZE = 4;
    private static final int ASSIGNMENT_SIZE = 20;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void process() throws Exception {
        List<Topic> topics = createTopics(TOPIC_SIZE);
        List<TopicAssignment> assignments = createAssignments(ASSIGNMENT_SIZE);

        List<LinkingResult> results = linker.process(topics, assignments);
    }

    @Test
    public void groupBy() throws Exception {
        List<Topic> topics = createTopics(TOPIC_SIZE);
        List<TopicAssignment> assignments = createAssignments(ASSIGNMENT_SIZE);
        Map<Integer, List<TopicAssignment>> map = linker.groupByTopic(assignments);

        Assert.assertThat(map.size(), is(topics.size()));
        topics.forEach(t -> Assert.assertTrue(map.containsKey(t.getTopic())));
        assignments.forEach(a -> Assert.assertTrue(map.get(a.getTopic()).contains(a)));


        Map<Integer, List<TopicAssignment>> map2 = linker.groupByTopic(new ArrayList<>());
        Assert.assertTrue(map2.isEmpty());
    }

    private List<Topic> createTopics(int topicSize) {
        List<Topic> topics = new ArrayList<>();
        for (int i = 0; i < topicSize; i++) {
            topics.add(new Topic(ImmutableSet.of(), i));
        }
        return topics;
    }

    private List<TopicAssignment> createAssignments(int assignmentSize) {
        List<TopicAssignment> assignments = new ArrayList<>();
        for (int i = 0; i < assignmentSize; i++) {
            assignments.add(new TopicAssignment(Character.valueOf((char) ((i + 65) % 127)).toString(), ImmutableSet.of(), i % TOPIC_SIZE));
        }
        return assignments;
    }
}
