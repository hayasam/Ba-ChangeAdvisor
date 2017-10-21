package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignment;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.ChangeAdvisorLinker;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.LabelRepository;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewsByTopLabelsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class LabelLinkerService {

    private final TransformedFeedbackRepository transformedFeedbackRepository;

    private final LabelRepository labelRepository;

    private final CodeElementRepository codeElementRepository;

    private final ChangeAdvisorLinker linker;

    @Autowired
    public LabelLinkerService(TransformedFeedbackRepository transformedFeedbackRepository, LabelRepository labelRepository, CodeElementRepository codeElementRepository, ChangeAdvisorLinker linker) {
        this.transformedFeedbackRepository = transformedFeedbackRepository;
        this.labelRepository = labelRepository;
        this.codeElementRepository = codeElementRepository;
        this.linker = linker;
    }

    /**
     * Retrieves the reviews based on the top N labels.
     * Fetches all reviews which contain these top labels.
     *
     * @param dto object representing the parameters we use to compute the top N labels
     *            (e.g. how many labels and for which app)
     * @return reviews for the top N labels.
     */
    public List<LinkingResult> link(String token, ReviewsByTopLabelsDto dto) {
        Label label = labelRepository.findByAppNameAndCategoryAndToken(dto.getApp(), dto.getCategory(), token);
        List<TransformedFeedback> feedback = transformedFeedbackRepository.findDistinctByArdocResultAppNameAndArdocResultCategoryAndTransformedSentenceContainingIgnoreCase(dto.getApp(), dto.getCategory(), label.getLabel());
        List<TopicAssignment> topicAssignments = feedback.stream().map(f -> new TopicAssignment(f.getSentence(), f.getBagOfWords(), 1)).collect(Collectors.toList());
        return linker.process(1, topicAssignments, codeElementRepository.findAll());
    }
}
