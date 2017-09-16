package ch.uzh.ifi.seal.changeadvisor.batch.job.reviews;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class ReviewReaderTest {

    @Autowired
    private ReviewRepository repository;

    private ReviewReader reader;

    @Before
    public void setUp() throws Exception {
        reader = new ReviewReader(repository, "com.sm.calculateme");
    }

    @Test
    public void read() throws Exception {
        reader = new ReviewReader(repository, "com.sm.calculateme");
        Review review;
        int count = 0;
        while ((review = reader.read()) != null) {
            count++;
        }

        Assert.assertTrue(count > 0);
        Assert.assertTrue(count == 100);

        reader = new ReviewReader(repository, "com.androbaby.game2048");
        count = 0;
        while ((review = reader.read()) != null) {
            count++;
        }

        Assert.assertTrue(count > 0);
        Assert.assertTrue(count == 102);
    }

}