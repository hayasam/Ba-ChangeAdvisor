package ch.uzh.ifi.seal.changeadvisor.web;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class SourceCodeDirectoryDto {

    @NotNull(message = "Path may not be null")
    @Size(min = 1, message = "Path may not be empty")
    private String path;

    private String projectName;

    private String username;

    private String password;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
