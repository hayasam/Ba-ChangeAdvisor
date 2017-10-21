package ch.uzh.ifi.seal.changeadvisor.service;

import ch.uzh.ifi.seal.changeadvisor.ChangeadvisorApplication;
import ch.uzh.ifi.seal.changeadvisor.MongoTestConfig;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewCategory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewDistributionReport;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {ChangeadvisorApplication.class, MongoTestConfig.class})
@ActiveProfiles("test")
public class ReviewAggregationServiceTest {

    private static final Logger logger = Logger.getLogger(ReviewAggregationServiceTest.class);

    @Autowired
    private ReviewAggregationService service;

    @Test
    public void groupyBy() throws Exception {
        ReviewDistributionReport reviewCategories = service.groupByCategories("com.frostwire.android");
        logger.info(reviewCategories.toString());
        ReviewCategory featureRequest = reviewCategories.findForCategory("FEATURE REQUEST");
        ReviewCategory informationSeeking = reviewCategories.findForCategory("INFORMATION SEEKING");
        ReviewCategory problemDiscovery = reviewCategories.findForCategory("PROBLEM DISCOVERY");
        ReviewCategory informationGiving = reviewCategories.findForCategory("INFORMATION GIVING");
        ReviewCategory other = reviewCategories.findForCategory("OTHER");

        Assert.assertThat(featureRequest.size(), is(130));
        Assert.assertThat(informationSeeking.size(), is(19));
        Assert.assertThat(problemDiscovery.size(), is(189));
        Assert.assertThat(informationGiving.size(), is(158));
        Assert.assertThat(other.size(), is(1091));
    }

}