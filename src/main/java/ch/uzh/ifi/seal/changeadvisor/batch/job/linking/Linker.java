package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.source.parser.CodeElement;

import java.util.Collection;
import java.util.List;

public interface Linker {

    List<LinkingResult> process(Collection<TopicAssignment> assignments, Collection<CodeElement> codeElements);

    List<LinkingResult> process(int topicId, Collection<TopicAssignment> assignments, Collection<CodeElement> codeElements);
}
