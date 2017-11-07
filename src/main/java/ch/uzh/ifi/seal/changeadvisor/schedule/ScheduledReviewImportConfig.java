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
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
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

    public void configureTasks() {
        if (taskRegistrar == null) {
            throw new IllegalStateException("Task Registrar not configured for ScheduledReviewImportConfig! " +
                    "Cannot schedule review import.");
        }

        logger.info("Configuring next scheduled review imports.");

        final Set<ReviewImportCronTask> cronTasks = new HashSet<>();
        Collection<Project> projects = projectService.findAll();
        projects.forEach(project -> {
            if (!StringUtils.isEmpty(project.getCronSchedule())) {
                cronTasks
                        .add(new ReviewImportCronTask(
                                () -> startReviewImport(project),
                                project.getCronSchedule(),
                                project.getGooglePlayId()
                        ));
            }
        });

        taskRegistrar.setCronTasksList(new ArrayList<>(cronTasks));
        taskRegistrar.afterPropertiesSet();
    }

    private void startReviewImport(final Project project) {
        try {
            logger.info(String.format("The time is now %s. Starting review import.", dateFormat.format(new Date())));
            Map<String, Object> params = createReviewImportParams(project);
            reviewImportService.reviewImport(params);

            ReviewsConfig mostRecentRun = new ReviewsConfig(new Date());
            project.setReviewsConfig(mostRecentRun);
            projectService.save(project);
        } catch (FailedToRunJobException e) {
            logger.error("Failed to start scheduled review import.", e);
        }
    }

    private Map<String, Object> createReviewImportParams(final Project project) {
        Map<String, Object> params = new HashMap<>();
        params.put("id", project.getId());
        params.put("limit", 5000);
        return params;
    }
}
