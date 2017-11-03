package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.ChangeadvisorApplication;
import ch.uzh.ifi.seal.changeadvisor.MongoTestConfig;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResultRepository;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedback;
import ch.uzh.ifi.seal.changeadvisor.batch.job.feedbackprocessing.TransformedFeedbackRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewCategoryReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewDistributionReport;
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
        ReviewCategoryReport featureRequest = reviewCategories.findForCategory("FEATURE REQUEST");
        ReviewCategoryReport informationSeeking = reviewCategories.findForCategory("INFORMATION SEEKING");
        ReviewCategoryReport problemDiscovery = reviewCategories.findForCategory("PROBLEM DISCOVERY");
        ReviewCategoryReport informationGiving = reviewCategories.findForCategory("INFORMATION GIVING");
        ReviewCategoryReport other = reviewCategories.findForCategory("OTHER");

        Assert.assertThat(featureRequest.getReviewCount(), is(this.featureReq.size()));
        Assert.assertThat(informationSeeking.getReviewCount(), is(this.infoSeeking.size()));
        Assert.assertThat(problemDiscovery.getReviewCount(), is(this.problem.size()));
        Assert.assertThat(informationGiving.getReviewCount(), is(this.infoG.size()));
        Assert.assertThat(other.getReviewCount(), is(this.other.size()));
    }

    @Test
    public void groupyByCountOnly() throws Exception {
        ReviewDistributionReport reviewCategories = service.groupByCategoriesCountOnly(APP_NAME);
        ReviewCategoryReport featureRequest = reviewCategories.findForCategory("FEATURE REQUEST");
        ReviewCategoryReport informationSeeking = reviewCategories.findForCategory("INFORMATION SEEKING");
        ReviewCategoryReport problemDiscovery = reviewCategories.findForCategory("PROBLEM DISCOVERY");
        ReviewCategoryReport informationGiving = reviewCategories.findForCategory("INFORMATION GIVING");
        ReviewCategoryReport other = reviewCategories.findForCategory("OTHER");

        Assert.assertThat(featureRequest.getReviewCount(), is(this.featureReq.size()));
        Assert.assertThat(informationSeeking.getReviewCount(), is(this.infoSeeking.size()));
        Assert.assertThat(problemDiscovery.getReviewCount(), is(this.problem.size()));
        Assert.assertThat(informationGiving.getReviewCount(), is(this.infoG.size()));
        Assert.assertThat(other.getReviewCount(), is(this.other.size()));
    }

}