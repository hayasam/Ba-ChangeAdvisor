package ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf;

import ch.uzh.ifi.seal.changeadvisor.service.ReviewAggregationService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewCategory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewDistributionReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewsByTopLabelsDto;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;

public class TopLabelTasklet implements Tasklet {

    private static final int MAX_LABELS_TO_COMPUTE = 100;

    private static final int MAX_NGRAM_SIZE = 3;

    private final ReviewAggregationService service;

    private final String appName;

    private final LabelRepository repository;

    public TopLabelTasklet(ReviewAggregationService service, String appName, LabelRepository repository) {
        this.service = service;
        this.appName = appName;
        this.repository = repository;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        repository.deleteAllByAppName(appName);
        ReviewDistributionReport reviewCategories = service.groupByCategories(appName);
        for (ReviewCategory category : reviewCategories) {
            for (int i = 1; i < MAX_NGRAM_SIZE; i++) {
                ReviewsByTopLabelsDto dto =
                        new ReviewsByTopLabelsDto(appName, category.getCategory(), MAX_LABELS_TO_COMPUTE, i);

                List<Label> labels = service.topNLabels(dto);
                repository.saveAll(labels);
            }
        }

        return RepeatStatus.FINISHED;
    }
}
