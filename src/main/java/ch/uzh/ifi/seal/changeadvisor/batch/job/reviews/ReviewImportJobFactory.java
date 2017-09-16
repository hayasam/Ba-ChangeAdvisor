package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ArdocStepConfig;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewAnalysisDto;
import org.apache.log4j.Logger;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

@Component
public class ReviewImportJobFactory {

    private static final Logger logger = Logger.getLogger(ReviewImportJobFactory.class);

    private static final String REVIEW_IMPORT = "reviewImport";

    private static final String REVIEW_ANALYSIS = "reviewAnalysis";

    private static final String STEP_NAME = "reviewImportStep";

    private static final String REVIEW_ANALYSIS_STEP = "reviewAnalysisStep";

    private final StepBuilderFactory stepBuilderFactory;

    private final JobBuilderFactory jobBuilderFactory;

    private final ArdocStepConfig ardocConfig;

    @Autowired
    public ReviewImportJobFactory(StepBuilderFactory stepBuilderFactory, JobBuilderFactory reviewImportJobBuilder, ArdocStepConfig ardocConfig) {
        this.stepBuilderFactory = stepBuilderFactory;
        this.jobBuilderFactory = reviewImportJobBuilder;
        this.ardocConfig = ardocConfig;
    }

    public Job job(Map<String, Object> params) {
        ArrayList<String> apps = getApps(params).orElseThrow(() -> new IllegalArgumentException("Apps to parse cannot be empty."));
        return jobBuilderFactory.get(REVIEW_IMPORT)
                .incrementer(new RunIdIncrementer())
                .flow(reviewImport(apps, params))
                .end()
                .build();
    }

    @SuppressWarnings("unchecked")
    private Optional<ArrayList<String>> getApps(Map<String, Object> params) {
        ArrayList<String> apps = (ArrayList<String>) params.get("apps");
        return Optional.ofNullable(apps);
    }

    private Step reviewImport(ArrayList<String> apps, Map<String, Object> params) {
        ReviewsConfigurationManager configManager = ReviewsConfigurationManager.from(params);
        return stepBuilderFactory.get(STEP_NAME)
                .allowStartIfComplete(true)
                .tasklet(new ReviewImportTasklet(apps, configManager.getConfig()))
                .build();
    }

    public Job reviewAnalysis(ReviewAnalysisDto dto) {
        String app = dto.getApp();
        return jobBuilderFactory.get(REVIEW_ANALYSIS)
                .incrementer(new RunIdIncrementer())
                .flow(ardocConfig.ardocAnalysis(app))
                .end()
                .build();
    }
}
