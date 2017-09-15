package ch.uzh.ifi.seal.changeadvisor;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignmentRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.source.parser.CodeElementRepository;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
public class ChangeadvisorApplication implements CommandLineRunner {

    private final CodeElementRepository codeElementRepository;

    private final TopicRepository topicRepository;

    private final TopicAssignmentRepository topicAssignmentRepository;

    private final TransformedFeedbackRepository transformedFeedbackRepository;

    private final ArdocResultRepository ardocResultRepository;

    private final JobRepository jobRepository;

    @Autowired
    public ChangeadvisorApplication(CodeElementRepository codeElementRepository, TopicRepository topicRepository, TopicAssignmentRepository topicAssignmentRepository, TransformedFeedbackRepository transformedFeedbackRepository, ArdocResultRepository ardocResultRepository, JobRepository jobRepository) {
        this.codeElementRepository = codeElementRepository;
        this.topicRepository = topicRepository;
        this.topicAssignmentRepository = topicAssignmentRepository;
        this.transformedFeedbackRepository = transformedFeedbackRepository;
        this.ardocResultRepository = ardocResultRepository;
        this.jobRepository = jobRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        codeElementRepository.deleteAll();
        topicAssignmentRepository.deleteAll();
        topicRepository.deleteAll();
        transformedFeedbackRepository.deleteAll();
        ardocResultRepository.deleteAll();


    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(ChangeadvisorApplication.class).web(true).run(args);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        threadPoolTaskExecutor.setCorePoolSize(5);
        threadPoolTaskExecutor.setMaxPoolSize(5);
        return threadPoolTaskExecutor;
    }

    @Bean
    public JobLauncher jobLauncher() {
        SimpleJobLauncher launcher = new SimpleJobLauncher();
        launcher.setTaskExecutor(taskExecutor());
        launcher.setJobRepository(jobRepository);
        return launcher;
    }
}
