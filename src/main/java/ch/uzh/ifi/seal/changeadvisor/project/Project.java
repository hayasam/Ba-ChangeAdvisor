package ch.uzh.ifi.seal.changeadvisor.project;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.scheduling.support.CronSequenceGenerator;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

@Document
public class Project {

    @Id
    private String id;

    @Indexed(unique = true)
    private String appName;

    private String path;

    private String remoteUrl;

    private String cronSchedule;

    Project() {
    }

    public Project(String appName, String path, String remoteUrl, String cronSchedule) {
        Assert.isTrue(!StringUtils.isEmpty(appName), "App name cannot be empty!");
        Assert.isTrue(checkCronExpression(cronSchedule), String.format("Cron expression is not valid. Got %s.", cronSchedule));
        this.appName = appName;
        this.path = path;
        this.remoteUrl = remoteUrl;
        this.cronSchedule = cronSchedule;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getCronSchedule() {
        return cronSchedule;
    }

    public void setCronSchedule(String cronSchedule) {
        this.cronSchedule = cronSchedule;
    }

    /**
     * A cron expression can either be empty (manual triggering of review import)
     * or it has to be a valid cron expression.
     *
     * @param expression expression to verify.
     * @return true iff expression is valid cron expression.
     */
    private boolean checkCronExpression(String expression) {
        return StringUtils.isEmpty(expression) || CronSequenceGenerator.isValidExpression(expression);
    }

    @Override
    public String toString() {
        return "Project{" +
                "id='" + id + '\'' +
                ", appName='" + appName + '\'' +
                ", path='" + path + '\'' +
                ", remoteUrl='" + remoteUrl + '\'' +
                ", cronSchedule='" + cronSchedule + '\'' +
                '}';
    }
}
