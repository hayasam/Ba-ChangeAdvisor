package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.ChangeadvisorApplication;
import ch.uzh.ifi.seal.changeadvisor.MongoTestConfig;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewCategory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewDistributionReport;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChangeadvisorApplication.class, MongoTestConfig.class})
@ActiveProfiles("test")
public class ReviewAggregationServiceTest {

    private static final Logger logger = Logger.getLogger(ReviewAggregationServiceTest.class);

    private ReviewAggregationService service;

    @Autowired
    private TransformedFeedbackRepository feedbackRepository;

    @Autowired
    private ArdocResultRepository ardocResultRepository;

    @Autowired
    private MongoTemplate testMongoTemplate;

    private static final String APP_NAME = "test.app";

    private List<TransformedFeedback> featureReq;

    private List<TransformedFeedback> infoSeeking;

    private List<TransformedFeedback> problem;

    private List<TransformedFeedback> infoG;

    private List<TransformedFeedback> other;

    @Before
    public void setUp() throws Exception {
        feedbackRepository.deleteByArdocResultAppName(APP_NAME);
        featureReq = createFeedbacks("FEATURE REQUEST");
        infoSeeking = createFeedbacks("INFORMATION SEEKING");
        problem = createFeedbacks("PROBLEM DISCOVERY");
        infoG = createFeedbacks("INFORMATION GIVING");
        other = createFeedbacks("OTHER");

        service = new ReviewAggregationService(testMongoTemplate, null, feedbackRepository, null);
    }

    private List<TransformedFeedback> createFeedbacks(final String category) {
        List<TransformedFeedback> feedbacks = new ArrayList<>();
        for (int i = 0; i < new Random().nextInt(10) + 50; i++) {
            TransformedFeedback t = new TransformedFeedback();
            ArdocResult result = new ArdocResult();
            result.setCategory(category);
            result.setAppName(APP_NAME);
            t.setArdocResult(result);
            t.setTransformedSentence(UUID.randomUUID().toString());
            feedbacks.add(t);
        }

        feedbacks.forEach(f -> f.setArdocResult(ardocResultRepository.save(f.getArdocResult())));
        return feedbackRepository.saveAll(feedbacks);
    }

    @Test
    public void groupyBy() throws Exception {

        ReviewDistributionReport reviewCategories = service.groupByCategories(APP_NAME);
        logger.info(reviewCategories.toString());
        ReviewCategory featureRequest = reviewCategories.findForCategory("FEATURE REQUEST");
        ReviewCategory informationSeeking = reviewCategories.findForCategory("INFORMATION SEEKING");
        ReviewCategory problemDiscovery = reviewCategories.findForCategory("PROBLEM DISCOVERY");
        ReviewCategory informationGiving = reviewCategories.findForCategory("INFORMATION GIVING");
        ReviewCategory other = reviewCategories.findForCategory("OTHER");

        Assert.assertThat(featureRequest.size(), is(this.featureReq.size()));
        Assert.assertThat(informationSeeking.size(), is(this.infoSeeking.size()));
        Assert.assertThat(problemDiscovery.size(), is(this.problem.size()));
        Assert.assertThat(informationGiving.size(), is(this.infoG.size()));
        Assert.assertThat(other.size(), is(this.other.size()));
    }

}