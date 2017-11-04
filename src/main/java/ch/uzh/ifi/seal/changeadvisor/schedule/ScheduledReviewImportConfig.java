package ch.uzh.ifi.seal.changeadvisor.schedule;

import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.service.FailedToRunJobException;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewImportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
@EnableScheduling
public class ScheduledReviewImportConfig implements SchedulingConfigurer {

    private static final Logger logger = Logger.getLogger(ScheduledReviewImportConfig.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final ReviewImportService reviewImportService;

    private final ProjectService projectService;

    @Autowired
    public ScheduledReviewImportConfig(ReviewImportService reviewImportService, ProjectService projectService) {
        this.reviewImportService = reviewImportService;
        this.projectService = projectService;
    }

    @Bean(destroyMethod = "shutdown")
    public ScheduledExecutorService taskExecutor() {
        return Executors.newScheduledThreadPool(100);
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(taskExecutor());

        Collection<Project> projects = projectService.findAll();
        projects.forEach(project -> {
            if (!StringUtils.isEmpty(project.getCronSchedule())) {
                taskRegistrar
                        .addTriggerTask(
                                () -> startReviewImport(project.getAppName()),
                                triggerContext -> setNextExecution(triggerContext, project.getCronSchedule()));
            }
        });
    }

    private Map<String, Object> createReviewImportParams(final String projectName) {
        Map<String, Object> params = new HashMap<>();
        params.put("apps", projectName);
        params.put("limit", 1000);
        return params;
    }

    private void startReviewImport(final String appName) {
        try {
            logger.info(String.format("The time is now %s. Starting review import.", dateFormat.format(new Date())));
            Map<String, Object> params = createReviewImportParams(appName);
            reviewImportService.reviewImport(params);
        } catch (FailedToRunJobException e) {
            logger.error("Failed to start scheduled review import.", e);
        }
    }

    private Date setNextExecution(TriggerContext triggerContext, final String cronExpression) {
        CronTrigger trigger = new CronTrigger(cronExpression);
        return trigger.nextExecutionTime(triggerContext);
    }
}
