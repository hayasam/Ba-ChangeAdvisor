package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.LabelRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    private final LabelRepository labelRepository;

    private final TransformedFeedbackRepository transformedFeedbackRepository;

    public LabelService(LabelRepository labelRepository, TransformedFeedbackRepository transformedFeedbackRepository) {
        this.labelRepository = labelRepository;
        this.transformedFeedbackRepository = transformedFeedbackRepository;
    }

    public List<Label> labels(final String appName) {
        return labelRepository.findTop20ByAppNameAndNgramSizeOrderByScoreDesc(appName, 1);
    }

    public List<TransformedFeedback> getFeedbackCorrespondingToLabel(final String token, final String appName, final String category) {
        Label label = labelRepository.findByAppNameAndCategoryAndToken(appName, category, token);
        return transformedFeedbackRepository
                .findDistinctByArdocResultAppNameAndArdocResultCategoryAndTransformedSentenceContainingIgnoreCase(
                        appName, category, label.getLabel());
    }

}
