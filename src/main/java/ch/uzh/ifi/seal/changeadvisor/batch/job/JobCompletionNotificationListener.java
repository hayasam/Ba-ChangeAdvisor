package ch.uzh.ifi.seal.changeadvisor.batch.job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

/**
 * Simple job completion listener.
 * Created by alex on 15.07.2017.
 */
@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger logger = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            long timeElapsed = jobExecution.getEndTime().getTime() - jobExecution.getStartTime().getTime();
            logger.info("Job finished in %.2f minutes", timeElapsed / 60.);
        }
    }
}