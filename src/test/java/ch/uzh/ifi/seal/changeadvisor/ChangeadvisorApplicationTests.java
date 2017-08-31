package ch.uzh.ifi.seal.changeadvisor;

import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicAssignmentRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.documentclustering.TopicRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.parser.CodeElementRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChangeadvisorApplication.class, MongoTestConfig.class})
@ActiveProfiles("test")
public class ChangeadvisorApplicationTests {

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

    @Before
    public void setUp() throws Exception {
        clearDb();
    }

    @After
    public void tearDown() throws Exception {
        clearDb();
    }

    private void clearDb() {
        codeElementRepository.deleteAll();
        topicRepository.deleteAll();
        topicAssignmentRepository.deleteAll();
        transformedFeedbackRepository.deleteAll();
        ardocResultRepository.deleteAll();
    }

    @Test
    public void contextLoads() {
    }
}
