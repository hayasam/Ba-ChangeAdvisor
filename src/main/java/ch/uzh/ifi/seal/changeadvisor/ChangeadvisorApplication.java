package ch.uzh.ifi.seal.changeadvisor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChangeadvisorApplication implements CommandLineRunner {

    @Autowired
    private CodeElementRepository codeElementRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private TopicAssignmentRepository topicAssignmentRepository;

    @Autowired
    private TransformedFeedbackRepository transformedFeedbackRepository;

    @Autowired
    private ArdocResultRepository ardocResultRepository;

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
//        SpringApplication.run(ChangeadvisorApplication.class, args);
    }

    @Autowired
    private JobRepository jobRepository;

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
