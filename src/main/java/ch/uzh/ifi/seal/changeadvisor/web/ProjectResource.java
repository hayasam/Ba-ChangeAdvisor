package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProjectResource {

    private final ProjectService service;

    @Autowired
    public ProjectResource(ProjectService service) {
        this.service = service;
    }

    @GetMapping("/project/{appName}")
    public Project getProjectByAppName(@PathVariable("appName") String appName) {
        return service.findByAppName(appName);
    }

    @PostMapping("/project")
    public Project saveProject(Project project) {
        return service.save(project);
    }
}
