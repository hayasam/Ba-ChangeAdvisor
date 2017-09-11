package ch.uzh.ifi.seal.changeadvisor.web;

import org.springframework.batch.core.JobExecution;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import java.util.HashMap;
import java.util.Map;

@Component
@SessionScope
public class SessionUtil {

    private Map<Long, JobExecution> jobs = new HashMap<>();

    public JobExecution getJob(Long id) {
        return jobs.get(id);
    }

    public void addJob(JobExecution jobExecution) {
        jobs.put(jobExecution.getJobId(), jobExecution);
    }

    public boolean hasJob(Long id) {
        return jobs.containsKey(id);
    }
}
