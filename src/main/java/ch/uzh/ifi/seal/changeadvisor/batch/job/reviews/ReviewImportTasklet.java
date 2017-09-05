package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import config.ConfigurationManager;
import extractors.Extractor;
import extractors.ExtractorFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.ArrayList;

public class ReviewImportTasklet implements Tasklet {

    private static final String EXTRACTOR = "reviews";

    private ArrayList<String> apps;

    private ConfigurationManager config;

    ReviewImportTasklet(ArrayList<String> apps, ConfigurationManager config) {
        this.apps = apps;
        this.config = config;
    }

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        Extractor extractor = extractor();
        extractor.extract();
        return null;
    }

    private Extractor extractor() {
        Extractor extractor = ExtractorFactory.getExtractor(apps, config, EXTRACTOR);
        extractor.printNumberOfInputApps();
        return extractor;
    }
}
