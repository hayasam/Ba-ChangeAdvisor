package ch.uzh.ifi.seal.changeadvisor.source;

public interface SourceCodeImporter {

    SourceCodeDirectory importSource();

    void setCredentials(String username, String password);
}
