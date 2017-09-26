package ch.uzh.ifi.seal.changeadvisor.web.dto;

import org.springframework.batch.core.StepExecution;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class ExecutionReport {

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private String stepName;

    private Date startTime;

    private Date endTime;

    private Date lastUpdated;

    private String detailMessage;

    private String exitCode;

    public ExecutionReport() {
    }

    public ExecutionReport(String stepName, Date startTime, Date endTime, Date lastUpdated, String detailMessage, String exitCode) {
        this.stepName = stepName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.lastUpdated = lastUpdated;
        this.detailMessage = detailMessage;
        this.exitCode = exitCode;
    }

    public String getStepName() {
        return stepName;
    }

    public void setStepName(String stepName) {
        this.stepName = stepName;
    }

    public String getStartTime() {
        return format.format(startTime);
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        if (endTime == null) {
            return "Pending";
        }
        return format.format(endTime);
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getLastUpdated() {
        if (lastUpdated == null) {
            return "Pending";
        }
        return format.format(lastUpdated);
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public void setDetailMessage(String detailMessage) {
        this.detailMessage = detailMessage;
    }

    public String getExitCode() {
        return exitCode;
    }

    public void setExitCode(String exitCode) {
        this.exitCode = exitCode;
    }

    public static ExecutionReport of(StepExecution stepExecution) {
        String message = "";
        if (hasFailureInContext(stepExecution)) {
            message = stepExecution.getFailureExceptions().get(0).getMessage();
        }
        if (hasProgressInContext(stepExecution)) {
            Map<String, Integer> progressMap = getProgressFromContext(stepExecution);
            message = progressMap.toString();
        }
        return new ExecutionReport(stepExecution.getStepName(), stepExecution.getStartTime(),
                stepExecution.getEndTime(), stepExecution.getLastUpdated(), message, stepExecution.getExitStatus().getExitCode());
    }

    private static boolean hasFailureInContext(StepExecution stepExecution) {
        return !stepExecution.getFailureExceptions().isEmpty();
    }

    private static boolean hasProgressInContext(StepExecution stepExecution) {
        return stepExecution.getExecutionContext().containsKey("extractor.progress");
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Integer> getProgressFromContext(StepExecution stepExecution) {
        return (Map<String, Integer>) stepExecution.getExecutionContext().get("extractor.progress");
    }
}