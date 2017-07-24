package ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering;

import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.springframework.batch.item.ItemProcessor;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by alex on 24.07.2017.
 */
public class TopicClustering implements ItemProcessor<List<TransformedFeedback>, TopicClusteringResult> {

    public TopicClustering() {
    }

    @Override
    public TopicClusteringResult process(List<TransformedFeedback> items) throws Exception {
        Set<TopicAssignment> collect = items.stream()
                .map(item -> new TopicAssignment(item.getSentence(), item.getBagOfWords(), 0))
                .collect(Collectors.toSet());
        List<Topic> topics = Lists.newArrayList(
                new Topic(Sets.newHashSet("0THIS", "IS", "A", "MOCK", "RESULT"), 0),
                new Topic(Sets.newHashSet("1THIS", "IS", "A", "MOCK", "RESULT"), 1),
                new Topic(Sets.newHashSet("2THIS", "IS", "A", "MOCK", "RESULT"), 2),
                new Topic(Sets.newHashSet("3THIS", "IS", "A", "MOCK", "RESULT"), 3),
                new Topic(Sets.newHashSet("4THIS", "IS", "A", "MOCK", "RESULT"), 4),
                new Topic(Sets.newHashSet("5THIS", "IS", "A", "MOCK", "RESULT"), 5),
                new Topic(Sets.newHashSet("6THIS", "IS", "A", "MOCK", "RESULT"), 6),
                new Topic(Sets.newHashSet("7THIS", "IS", "A", "MOCK", "RESULT"), 7),
                new Topic(Sets.newHashSet("8THIS", "IS", "A", "MOCK", "RESULT"), 8),
                new Topic(Sets.newHashSet("9THIS", "IS", "A", "MOCK", "RESULT"), 9)
        );
        return new TopicClusteringResult(topics, collect);
    }
}
