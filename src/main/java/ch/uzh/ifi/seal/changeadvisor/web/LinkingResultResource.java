package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResult;
import ch.uzh.ifi.seal.changeadvisor.batch.job.linking.LinkingResultRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LinkingResultResource {

    private final LinkingResultRepository repository;

    private final Job changeAdvisor;

    private final JobLauncher jobLauncher;

    @Autowired
    public LinkingResultResource(LinkingResultRepository repository, Job changeAdvisor, JobLauncher jobLauncher) {
        this.repository = repository;
        this.changeAdvisor = changeAdvisor;
        this.jobLauncher = jobLauncher;
    }

    @GetMapping(path = "/results")
    public List<LinkingResult> results() {
        return repository.findAll();
    }

    @GetMapping(path = "/job")
    public void startJob() throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        jobLauncher.run(changeAdvisor, new JobParameters());
    }

}
