package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.parser.CodeElement;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

        List<LinkingResult> results = linker.process(topics, assignments, new ArrayList<>());
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

    @Test
    public void codeComponentWordMap() throws Exception {
        CodeElement c1 = new CodeElement("HelloWorld", Sets.newHashSet("hello", "world"));
        CodeElement c2 = new CodeElement("HelloWorld2", Sets.newHashSet("System", "out"));
        CodeElement c3 = new CodeElement(null, null);
        ArrayList<CodeElement> codeElements = Lists.newArrayList(c1, c2, c3);

        Map<CodeElement, Set<String>> codeElementSetMap = linker.codeComponentWordMap(codeElements);
        Assert.assertThat(codeElementSetMap.size(), is(2));
        Assert.assertTrue(codeElementSetMap.containsKey(c1));
        Assert.assertTrue(codeElementSetMap.containsKey(c2));
        Assert.assertThat(codeElementSetMap.get(c1), is(c1.getBag()));
        Assert.assertThat(codeElementSetMap.get(c2), is(c2.getBag()));
        Assert.assertFalse(codeElementSetMap.containsKey(c3));

        codeElementSetMap = linker.codeComponentWordMap(new ArrayList<>());
        Assert.assertThat(codeElementSetMap.size(), is(0));
    }
}
