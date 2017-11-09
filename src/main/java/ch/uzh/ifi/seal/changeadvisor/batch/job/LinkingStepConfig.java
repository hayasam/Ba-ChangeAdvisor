package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Cluster;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicClusteringResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.*;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.labels.LabelFeedbackReader;
import ch.uzh.ifi.seal.changeadvisor.service.LabelService;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinkingStepConfig {

    private static final String STEP_NAME = "linking";
    private static final String STEP_NAME_TFIDF = "linking_tfidf_clusters";

    private final StepBuilderFactory stepBuilderFactory;

    private final BulkClusterReader bulkClusterReader;

    private final BulkClusterProcessor bulkClusterProcessor;

    private final ClusterReader clusterReader;

    private final ClusterProcessor clusterProcessor;

    private final LabelService labelService;

    private final LinkingResultRepository resultRepository;

    @Autowired
    public LinkingStepConfig(StepBuilderFactory stepBuilderFactory, BulkClusterReader bulkClusterReader,
                             BulkClusterProcessor bulkClusterProcessor, ClusterReader clusterReader,
                             ClusterProcessor clusterProcessor, LabelService labelService,
                             LinkingResultRepository resultRepository) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.bulkClusterReader = bulkClusterReader;
        this.bulkClusterProcessor = bulkClusterProcessor;
        this.clusterReader = clusterReader;
        this.clusterProcessor = clusterProcessor;
        this.labelService = labelService;
        this.resultRepository = resultRepository;
    }

    @Bean
    public Step bulkClusterLinking() {
        return stepBuilderFactory.get(STEP_NAME)
                .<TopicClusteringResult, List<LinkingResult>>chunk(1)
                .reader(bulkClusterReader)
                .processor(bulkClusterProcessor)
                .writer(new ClusterWriter(resultRepository, LinkingResult.ClusterType.HDP, null))
                .build();
    }

    @Bean
    public Step clusterLinking() {
        return stepBuilderFactory.get(STEP_NAME)
                .<Cluster, List<LinkingResult>>chunk(1)
                .reader(clusterReader)
                .processor(clusterProcessor)
                .writer(new ClusterWriter(resultRepository, LinkingResult.ClusterType.HDP, null))
                .build();
    }

    public Step clusterLinking(final String googlePlayId) {
        return stepBuilderFactory.get(STEP_NAME)
                .<Cluster, List<LinkingResult>>chunk(1)
                .reader(labelFeedbackReader(googlePlayId))
                .processor(clusterProcessor)
                .writer(new ClusterWriter(resultRepository, LinkingResult.ClusterType.TFIDF, googlePlayId))
                .build();
    }

    private LabelFeedbackReader labelFeedbackReader(final String googlePlayId) {
        return new LabelFeedbackReader(labelService, googlePlayId);
    }
}
