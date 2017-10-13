package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import edu.emory.mathcs.backport.java.util.Collections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProjectResource {

    private final ProjectService service;

    @Autowired
    public ProjectResource(ProjectService service) {
        this.service = service;
    }

    @GetMapping("/project")
    public List<Project> getProjects() {
        List<Project> projects = service.findAll();
        Collections.sort(projects);
        return projects;
    }

    @GetMapping("/project/{projectId}")
    public Project getProjectById(@PathVariable("projectId") final String projectId) {
        return service.findById(projectId);
    }

    @PostMapping("/project")
    public ResponseEntity<Project> saveProject(@RequestBody Project project) {
        if (project == null || !project.hasValidCronExpression()) {
            return ResponseEntity.badRequest().body(project);
        }

        Project savedProject = service.save(project);
        return ResponseEntity.ok(savedProject);
    }
}
