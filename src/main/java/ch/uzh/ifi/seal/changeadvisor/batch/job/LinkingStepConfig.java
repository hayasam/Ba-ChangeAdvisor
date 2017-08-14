package ch.uzh.ifi.seal.changeadvisor.batch.job;

import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.Cluster;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicClusteringResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.*;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class LinkingStepConfig {

    private static final String STEP_NAME = "linking";

    private final StepBuilderFactory stepBuilderFactory;

    private final BulkClusterReader bulkClusterReader;

    private final BulkClusterProcessor bulkClusterProcessor;

    private final ClusterReader clusterReader;

    private final ClusterProcessor clusterProcessor;

    private final ClusterWriter clusterWriter;


    @Autowired
    public LinkingStepConfig(StepBuilderFactory stepBuilderFactory, BulkClusterReader bulkClusterReader, BulkClusterProcessor bulkClusterProcessor, ClusterReader clusterReader, ClusterProcessor clusterProcessor, ClusterWriter clusterWriter) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.bulkClusterReader = bulkClusterReader;
        this.bulkClusterProcessor = bulkClusterProcessor;
        this.clusterReader = clusterReader;
        this.clusterProcessor = clusterProcessor;
        this.clusterWriter = clusterWriter;
    }

    @Bean
    public Step bulkClusterLinking() {
        return stepBuilderFactory.get(STEP_NAME)
                .<TopicClusteringResult, List<LinkingResult>>chunk(1)
                .reader(bulkClusterReader)
                .processor(bulkClusterProcessor)
                .writer(clusterWriter)
                .build();
    }

    @Bean
    public Step clusterLinking() {
        return stepBuilderFactory.get(STEP_NAME)
                .<Cluster, List<LinkingResult>>chunk(1)
                .reader(clusterReader)
                .processor(clusterProcessor)
                .writer(clusterWriter)
                .build();
    }
}
