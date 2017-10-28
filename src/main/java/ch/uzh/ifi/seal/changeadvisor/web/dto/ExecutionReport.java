package ch.uzh.ifi.seal.changeadvisor.web.dto;

import com.google.common.collect.ImmutableList;

import java.text.SimpleDateFormat;
import java.util.List;

public class ExecutionReport {

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<StepExecutionReport> stepReports;

    private final String jobName;

    private final String startTime;

    private final String endTime;

    private final String lastUpdated;

    public ExecutionReport(String jobName, List<StepExecutionReport> stepReports) {
        this.jobName = jobName;
        this.stepReports = stepReports;

        StepExecutionReport firstReport = stepReports.get(0);
        this.startTime = firstReport.getStartTime();

        StepExecutionReport lastReport = stepReports.get(stepReports.size() - 1);
        this.endTime = lastReport.getEndTime();
        this.lastUpdated = lastReport.getLastUpdated();
    }

    public String getJobName() {
        return jobName;
    }

    public List<StepExecutionReport> getStepReports() {
        return ImmutableList.copyOf(stepReports);
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }
}