package ch.uzh.ifi.seal.changeadvisor.source;

import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class SourceCodeDirectory {

    private String id;

    private String projectName;

    private String path;

    public SourceCodeDirectory() {
    }

    public SourceCodeDirectory(String projectName, String path) {
        this.projectName = projectName;
        this.path = path;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "SourceCodeDirectory{" +
                "projectName='" + projectName + '\'' +
                ", path='" + path + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SourceCodeDirectory that = (SourceCodeDirectory) o;

        if (projectName != null ? !projectName.equals(that.projectName) : that.projectName != null) return false;
        return path != null ? path.equals(that.path) : that.path == null;
    }

    @Override
    public int hashCode() {
        int result = projectName != null ? projectName.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }
}
