package ch.uzh.ifi.seal.changeadvisor.service;

import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JobService {

    private final JobLauncher jobLauncher;

    @Autowired
    public JobService(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    public JobExecution run(Job job) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        return run(job, parametersWithCurrentTimestamp());
    }

    public JobExecution run(Job job, JobParameters parameters) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        if (job == null || parameters == null) {
            throw new IllegalArgumentException(String.format("Job and parameters must both be not null. Got %s, %s", job, parameters));
        }
        return jobLauncher.run(job, parameters);
    }

    JobParameters parametersWithCurrentTimestamp() {
        return new JobParametersBuilder().addDate("timestamp", new Date()).toJobParameters();
    }
}
