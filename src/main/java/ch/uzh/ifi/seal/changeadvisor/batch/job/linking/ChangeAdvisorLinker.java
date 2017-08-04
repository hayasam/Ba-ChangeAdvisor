package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import com.google.common.collect.ImmutableList;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChangeAdvisorLinker implements Linker {

    @Override
    public List<LinkingResult> process(Collection<Topic> topics, Collection<TopicAssignment> assignments) {
        return ImmutableList.of();
    }

    Map<Integer, List<TopicAssignment>> groupByTopic(Collection<TopicAssignment> assignments) {
        return assignments.stream().collect(Collectors.groupingBy(TopicAssignment::getTopic));
    }
}
