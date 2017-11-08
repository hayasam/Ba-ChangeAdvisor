package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.ChangeAdvisorLinker;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.LabelRepository;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElement;
import ch.uzh.ifi.seal.changeadvisor.source.model.CodeElementRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewsByTopLabelsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
     * Runs the ChangeAdvisor linker with the reviews fetched from the label and code elements.
     *
     * @param dto object containing the app name and category for reviews.
     * @return changeadvisor linking results.
     */
    public List<LinkingResult> link(String token, ReviewsByTopLabelsDto dto) {
        List<TransformedFeedback> feedback = getFeedbackCorrespondingToLabel(token, dto.getApp(), dto.getCategory());
        List<CodeElement> codeElements = codeElementRepository.findByAppName(dto.getApp());
        return linker.process(1, feedback, codeElements);
    }

    private List<TransformedFeedback> getFeedbackCorrespondingToLabel(final String token, final String appName, final String category) {
        Label label = labelRepository.findByAppNameAndCategoryAndToken(appName, category, token);
        return transformedFeedbackRepository
                .findDistinctByArdocResultAppNameAndArdocResultCategoryAndTransformedSentenceContainingIgnoreCase(
                        appName, category, label.getLabel());
    }
}
