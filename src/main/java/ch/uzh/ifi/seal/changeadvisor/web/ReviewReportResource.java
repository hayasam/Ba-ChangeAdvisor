package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.batch.job.tfidf.Label;
import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import ch.uzh.ifi.seal.changeadvisor.service.ReviewAggregationService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.LabelWithReviews;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewDistributionReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ReviewsByTopLabelsDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ReviewReportResource {

    private final ProjectService projectService;

    private final ReviewAggregationService aggregationService;

    public ReviewReportResource(ProjectService projectService, ReviewAggregationService aggregationService) {
        this.projectService = projectService;
        this.aggregationService = aggregationService;
    }

    @GetMapping(path = "reviews/{projectId}/distribution")
    public ReviewDistributionReport distributionReport(@PathVariable("projectId") String projectId, @RequestParam("countOnly") boolean countOnly) {
        Project project = projectService.findById(projectId);
        if (countOnly) {
            return aggregationService.groupByCategoriesCountOnly(project.getAppName());
        }
        return aggregationService.groupByCategories(project.getAppName());
    }

    @PostMapping(path = "reviews/top")
    public List<Label> topNLabels(@RequestBody ReviewsByTopLabelsDto dto) {
        return aggregationService.topNLabels(dto);
    }

    @PostMapping(path = "reviews/labels")
    public List<LabelWithReviews> reviewsByTopNLabels(@RequestBody ReviewsByTopLabelsDto dto) {
        return aggregationService.reviewsByTopNLabels(dto);
    }

    @PostMapping(path = "reviews/labels/category")
    public List<LabelWithReviews> reviewsByTopNLabelsByCategory(@RequestBody ReviewsByTopLabelsDto dto) {
        return aggregationService.reviewsByTopNLabelsByCategory(dto);
    }

}
