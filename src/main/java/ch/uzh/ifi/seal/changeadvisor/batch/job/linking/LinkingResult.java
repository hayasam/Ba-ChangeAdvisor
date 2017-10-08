package ch.uzh.ifi.seal.changeadvisor.batch.job.linking;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Collection;

@Document
public class LinkingResult implements Comparable<LinkingResult> {

    @Id
    private String id;

    private int clusterId;

    private Collection<String> reviews;

    private Collection<String> clusterBag;

    private Collection<String> codeComponentBag;

    private String codeComponentName;

    private Double similarity;

    public LinkingResult() {
    }

    public LinkingResult(int clusterId, Collection<String> reviews, Collection<String> clusterBag, Collection<String> codeComponentBag, String codeComponentName, Double similarity) {
        this.clusterId = clusterId;
        this.reviews = reviews;
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

    public Collection<String> getReviews() {
        return reviews;
    }

    public void setReviews(Collection<String> reviews) {
        this.reviews = reviews;
    }

    public Collection<String> getClusterBag() {
        return clusterBag;
    }

    public void setClusterBag(Collection<String> clusterBag) {
        this.clusterBag = clusterBag;
    }

    public Collection<String> getCodeComponentBag() {
        return codeComponentBag;
    }

    public void setCodeComponentBag(Collection<String> codeComponentBag) {
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
