package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Topic;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.parser.CodeElement;

import java.util.Collection;
import java.util.List;

public interface Linker {

    List<LinkingResult> process(Collection<Topic> topics, Collection<TopicAssignment> assignments, Collection<CodeElement> codeElements);
}
