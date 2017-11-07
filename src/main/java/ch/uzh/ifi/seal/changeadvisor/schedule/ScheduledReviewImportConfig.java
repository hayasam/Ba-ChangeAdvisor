package ch.uzh.ifi.seal.changeadvisor.schedule;

import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.project.ReviewsConfig;
import ch.uzh.ifi.seal.changeadvisor.service.FailedToRunJobException;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewImportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Component
@EnableScheduling
public class ScheduledReviewImportConfig implements SchedulingConfigurer {

    private static final Logger logger = Logger.getLogger(ScheduledReviewImportConfig.class);

    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final ReviewImportService reviewImportService;

    private final ProjectService projectService;

    private ScheduledTaskRegistrar taskRegistrar;

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
    public void configureTasks(@NotNull ScheduledTaskRegistrar taskRegistrar) {
        if (this.taskRegistrar == null) {
            this.taskRegistrar = taskRegistrar;
        }
        this.taskRegistrar.setScheduler(taskExecutor());
        configureTasks();
    }

    private void configureTasks() {
        if (taskRegistrar == null) {
            throw new IllegalStateException("Task Registrar not configured for ScheduledReviewImportConfig! " +
                    "Cannot schedule review import.");
        }

        logger.info("Configuring next scheduled review imports.");
        Collection<Project> projects = projectService.findAll();

        projects.forEach(project -> {

            if (!StringUtils.isEmpty(project.getCronSchedule())) {
                Trigger nextExecutionTrigger = trigger(project.getId());
                taskRegistrar.addTriggerTask(
                        () -> startReviewImport(project),
                        nextExecutionTrigger
                );
            }
        });
    }

    private void startReviewImport(final Project project) {
        try {
            logger.info(String.format("The time is now %s. Starting review import for [%s].", dateFormat.format(new Date()), project.getAppName()));
            Map<String, Object> params = createReviewImportParams(project);
            reviewImportService.reviewImport(params);

            ReviewsConfig mostRecentRun = new ReviewsConfig(new Date());
            project.setReviewsConfig(mostRecentRun);
            Optional<Project> updatedProject = projectService.findById(project.getId());
            updatedProject.ifPresent(p -> {
                p.setReviewsConfig(mostRecentRun);
                projectService.save(p);
            });

        } catch (FailedToRunJobException e) {
            logger.error(String.format("Failed to start scheduled review import for [%s].", project.getAppName()), e);
        }
    }

    private Map<String, Object> createReviewImportParams(final Project project) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", project.getId());
        params.put("limit", 5000);
        return params;
    }

    private Trigger trigger(final String projectId) {
        return triggerContext -> {
            Project project = projectService.findById(projectId).orElseThrow(IllegalArgumentException::new);
            CronSequenceGenerator sequenceGenerator = new CronSequenceGenerator(project.getCronSchedule());

            Date next = sequenceGenerator.next(new Date());
            Calendar nextExecutionTime = new GregorianCalendar();
            Date lastActualExecutionTime = triggerContext.lastActualExecutionTime();
            nextExecutionTime.setTime(next);
            logger.info(String.format("Setting next execution time for [%s]: %s", project.getGooglePlayId(), next));
            return nextExecutionTime.getTime();
        };
    }
}
