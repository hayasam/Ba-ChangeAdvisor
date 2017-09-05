package ch.uzh.ifi.seal.changeadvisor.web;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@SessionScope
public class SessionUtil {

    private Map<Long, JobExecution> executions;

    public SessionUtil() {
        executions = new ConcurrentHashMap<>();
    }

    public void putJob(JobExecution jobExecution) {
        executions.put(jobExecution.getJobId(), jobExecution);
    }

    public BatchStatus getStatus(Long jobId) {
        if (!executions.containsKey(jobId)) {
            throw new IllegalArgumentException(String.format("No job with id %d", jobId));
        }
        return executions.get(jobId).getStatus();
    }
}
