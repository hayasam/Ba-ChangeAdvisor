package ch.uzh.ifi.seal.changeadvisor.service;


import ch.uzh.ifi.seal.changeadvisor.batch.job.ardoc.ArdocResult;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewCategory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewDistributionReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
public class ReviewAggregationService {

    private final MongoTemplate mongoOperations;

    @Autowired
    public ReviewAggregationService(MongoTemplate mongoOperations) {
        this.mongoOperations = mongoOperations;
    }

    public ReviewDistributionReport groupByCategories(final String appName) {
        TypedAggregation<ArdocResult> categoryAggregation = Aggregation.newAggregation(ArdocResult.class,
                Aggregation.match(Criteria.where("appName").is(appName)),
                Aggregation.group("category").first("category").as("category") // set group by field and save it as 'as' in resulting object.
                        .push("$$ROOT").as("reviews") // push entire document to field 'pushTo' in ReviewCategory.
        );

        AggregationResults<ReviewCategory> groupResults =
                mongoOperations.aggregate(categoryAggregation, ArdocResult.class, ReviewCategory.class);

        return new ReviewDistributionReport(groupResults.getMappedResults());
    }
}
