package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.service.SourceCodeService;
import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectoryRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.ExecutionReport;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class SourceCodeController {

    private static final Logger logger = Logger.getLogger(SourceCodeController.class);

    private final SourceCodeService sourceCodeService;

    private final SourceCodeDirectoryRepository repository;

    private final SessionUtil sessionUtil;

    @Autowired
    public SourceCodeController(SourceCodeService sourceCodeService, SourceCodeDirectoryRepository repository, SessionUtil sessionUtil) {
        this.sourceCodeService = sourceCodeService;
        this.repository = repository;
        this.sessionUtil = sessionUtil;
    }

    @GetMapping(path = "source")
    public Collection<SourceCodeDirectory> directories() {
        List<SourceCodeDirectory> all = repository.findAll();
        return all;
    }

    @GetMapping(path = "source/{appName}")
    public SourceCodeDirectory directory(@PathVariable(name = "appName") String appName) {
        Optional<SourceCodeDirectory> project = repository.findByProjectName(appName);
        return project.orElse(new SourceCodeDirectory());
    }

    @PostMapping(path = "source")
    public long downloadSourceCode(@RequestBody @Valid SourceCodeDirectoryDto dto) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        logger.info(String.format("Adding directory %s", dto.getPath()));
        JobExecution jobExecution = sourceCodeService.startSourceCodeDownload(dto);
        sessionUtil.addJob(jobExecution);
        return jobExecution.getJobId();
    }

    @GetMapping(path = "source/status/{jobId}")
    @ResponseBody
    public Collection<ExecutionReport> status(@PathVariable("jobId") Long jobId) {
        if (sessionUtil.hasJob(jobId)) {
            JobExecution job = sessionUtil.getJob(jobId);
            Collection<StepExecution> stepExecutions = job.getStepExecutions();
            return stepExecutions
                    .stream()
                    .map(ExecutionReport::of)
                    .collect(Collectors.toList());
        }
        throw new IllegalArgumentException(String.format("No job found for job id: %d", jobId));
    }

}
