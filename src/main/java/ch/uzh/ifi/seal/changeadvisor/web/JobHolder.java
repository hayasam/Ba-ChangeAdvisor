package ch.uzh.ifi.seal.changeadvisor.web;

import ch.uzh.ifi.seal.changeadvisor.web.dto.ExecutionReport;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepExecution;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class JobHolder {

    private Map<Long, JobExecution> jobs = new HashMap<>();

    private JobExecution getJob(Long id) {
        return jobs.get(id);
    }

    public void addJob(JobExecution jobExecution) {
        if (jobExecution != null) {
            jobs.put(jobExecution.getJobId(), jobExecution);
        } else {
            throw new NullPointerException("Job execution cannot be null!");
        }
    }

    public void addJobs(Collection<JobExecution> jobs) {
        jobs.forEach(this::addJob);
    }

    public boolean hasJob(Long id) {
        return jobs.containsKey(id);
    }

    public int size() {
        return jobs.size();
    }

    public boolean isEmpty() {
        return jobs.isEmpty();
    }

    public void clear() {
        jobs.clear();
    }

    public Collection<ExecutionReport> executionReportForJob(Long jobId) {
        if (jobId != null && hasJob(jobId)) {
            JobExecution job = getJob(jobId);
            Collection<StepExecution> stepExecutions = job.getStepExecutions();
            return stepExecutions
                    .stream()
                    .map(ExecutionReport::of)
                    .collect(Collectors.toList());
        }
        throw new IllegalArgumentException(String.format("No job found for job id: %d", jobId));
    }
}
