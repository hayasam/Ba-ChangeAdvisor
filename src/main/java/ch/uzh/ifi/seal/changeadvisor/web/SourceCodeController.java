package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.service.FailedToRunJobException;
import ch.uzh.ifi.seal.changeadvisor.service.SourceCodeService;
import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectoryRepository;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.JobExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.Optional;

@RestController
public class SourceCodeController {

    private static final Logger logger = Logger.getLogger(SourceCodeController.class);

    private final SourceCodeService sourceCodeService;

    private final SourceCodeDirectoryRepository repository;

    @Autowired
    public SourceCodeController(SourceCodeService sourceCodeService, SourceCodeDirectoryRepository repository) {
        this.sourceCodeService = sourceCodeService;
        this.repository = repository;
    }

    @GetMapping(path = "source")
    public Collection<SourceCodeDirectory> directories() {
        return repository.findAll();
    }

    @GetMapping(path = "source/{appName}")
    public SourceCodeDirectory directory(@PathVariable(name = "appName") String appName) {
        Optional<SourceCodeDirectory> project = repository.findByProjectName(appName);
        return project.orElse(new SourceCodeDirectory());
    }

    @PostMapping(path = "source")
    public long downloadSourceCode(@RequestBody @Valid SourceCodeDirectoryDto dto) throws FailedToRunJobException {
        logger.info(String.format("Adding directory %s", dto.getPath()));
        JobExecution jobExecution = sourceCodeService.startSourceCodeDownload(dto);
        return jobExecution.getJobId();
    }

    @PostMapping(path = "source/processing/{appName}")
    public long processSourceCode(@PathVariable(name = "appName") String appName) throws FailedToRunJobException {
        if (StringUtils.isEmpty(appName)) {
            throw new IllegalArgumentException(String.format("Invalid Path Variable \"%s\"", appName));
        }
        JobExecution jobExecution = sourceCodeService.startSourceCodeProcessing(appName);
        return jobExecution.getJobId();
    }

}
