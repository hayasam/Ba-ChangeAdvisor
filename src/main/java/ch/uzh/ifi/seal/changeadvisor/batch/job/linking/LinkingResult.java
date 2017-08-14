package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Set;

@Document
public class LinkingResult implements Comparable<LinkingResult> {

    @Id
    private String id;

    private int clusterId;

    private Set<String> clusterBag;

    private Set<String> codeComponentBag;

    private String codeComponentName;

    private Double similarity;

    public LinkingResult() {
    }

    public LinkingResult(int clusterId, Set<String> clusterBag, Set<String> codeComponentBag, String codeComponentName, Double similarity) {
        this.clusterId = clusterId;
        this.clusterBag = clusterBag;
        this.codeComponentBag = codeComponentBag;
        this.codeComponentName = codeComponentName;
        this.similarity = similarity;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getClusterId() {
        return clusterId;
    }

    public void setClusterId(int clusterId) {
        this.clusterId = clusterId;
    }

    public Set<String> getClusterBag() {
        return clusterBag;
    }

    public void setClusterBag(Set<String> clusterBag) {
        this.clusterBag = clusterBag;
    }

    public Set<String> getCodeComponentBag() {
        return codeComponentBag;
    }

    public void setCodeComponentBag(Set<String> codeComponentBag) {
        this.codeComponentBag = codeComponentBag;
    }

    public String getCodeComponentName() {
        return codeComponentName;
    }

    public void setCodeComponentName(String codeComponentName) {
        this.codeComponentName = codeComponentName;
    }

    public Double getSimilarity() {
        return similarity;
    }

    public void setSimilarity(Double similarity) {
        this.similarity = similarity;
    }

    @Override
    public int compareTo(@NotNull LinkingResult o) {
        return codeComponentName.compareTo(o.codeComponentName);
    }

    @Override
    public String toString() {
        return "LinkingResult{" +
                "id='" + id + '\'' +
                ", clusterId=" + clusterId +
                ", clusterBag=" + clusterBag +
                ", codeComponentBag=" + codeComponentBag +
                ", codeComponentName='" + codeComponentName + '\'' +
                ", similarity=" + similarity +
                '}';
    }
}
