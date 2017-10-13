package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.project.Project;
import ch.uzh.ifi.seal.changeadvisor.service.FailedToRunJobException;
import ch.uzh.ifi.seal.changeadvisor.service.ProjectService;
import ch.uzh.ifi.seal.changeadvisor.service.SourceCodeService;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class SourceCodeController {

    private static final Logger logger = Logger.getLogger(SourceCodeController.class);

    private final SourceCodeService sourceCodeService;

    private final ProjectService projectService;

    @Autowired
    public SourceCodeController(SourceCodeService sourceCodeService, ProjectService projectService) {
        this.sourceCodeService = sourceCodeService;
        this.projectService = projectService;
    }

    @PostMapping(path = "source")
    public long downloadSourceCode(@RequestBody @Valid SourceCodeDirectoryDto dto) throws FailedToRunJobException {
        Assert.isTrue(projectService.projectExists(dto.getProjectName()), "Project doesn't exists!");
        JobExecution jobExecution = sourceCodeService.startSourceCodeDownload(dto);
        return jobExecution.getJobId();
    }

    @PostMapping(path = "source/processing")
    public long processSourceCode(@RequestBody Project project) throws FailedToRunJobException {
        if (StringUtils.isEmpty(project.getAppName())) {
            throw new IllegalArgumentException(String.format("Invalid Path Variable \"%s\"", project.getAppName()));
        }
        JobExecution jobExecution = sourceCodeService.startSourceCodeProcessing(project.getAppName());
        return jobExecution.getJobId();
    }

}
