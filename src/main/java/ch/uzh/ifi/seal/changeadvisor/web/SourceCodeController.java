package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.service.SourceCodeService;
import ch.uzh.ifi.seal.changeadvisor.source.SourceCodeDirectory;
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
import java.util.stream.Collectors;

@RestController
public class SourceCodeController {

    private static final Logger logger = Logger.getLogger(SourceCodeController.class);

    private final SourceCodeService sourceCodeService;

    private final SessionUtil sessionUtil;


    @Autowired
    public SourceCodeController(SourceCodeService sourceCodeService, SessionUtil sessionUtil) {
        this.sourceCodeService = sourceCodeService;
        this.sessionUtil = sessionUtil;
    }

    @PostMapping(path = "source/directory")
    public SourceCodeDirectory addSourceDirectory(@RequestBody @Valid SourceCodeDirectoryDto dto) {
        logger.info(String.format("Adding directory %s", dto.getPath()));
        SourceCodeDirectory directory = sourceCodeService.addSourceDirectory(dto);
        logger.info(String.format("Added directory %s", directory));
        return directory;
    }

    @PostMapping(path = "source")
    public long downloadSourceCode(@RequestBody @Valid SourceCodeDirectoryDto dto) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
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
