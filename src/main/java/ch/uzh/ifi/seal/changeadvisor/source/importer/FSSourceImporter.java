package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FSSourceImporter implements SourceCodeImporter {

    private static final Logger logger = Logger.getLogger(FSSourceImporter.class);

    private final String path;

    FSSourceImporter(String path) {
        this.path = path;
    }

    @Override
    public void setCredentials(String username, String password) {
        logger.debug("Nothing to do here!");
    }

    @Override
    public SourceCodeDirectory importSource() {
        Path path = Paths.get(this.path);
        Assert.isTrue(path.toFile().exists() && path.toFile().isDirectory(), "Path doesn't exist or is not directory");
        return new SourceCodeDirectory(path.getFileName().toString(), path.toAbsolutePath().toString());
    }
}
