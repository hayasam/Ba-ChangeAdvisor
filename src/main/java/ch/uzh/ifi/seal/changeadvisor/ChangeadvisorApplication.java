package ch.uzh.ifi.seal.changeadvisor;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignmentRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.parser.CodeElementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

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

	public static void main(String[] args) {
        new SpringApplicationBuilder(ChangeadvisorApplication.class).web(false).run(args);
//        SpringApplication.run(ChangeadvisorApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        codeElementRepository.deleteAll();
        topicAssignmentRepository.deleteAll();
        topicRepository.deleteAll();
        transformedFeedbackRepository.deleteAll();
        ardocResultRepository.deleteAll();


    }
}
