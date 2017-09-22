package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

public class FSSourceImporter implements SourceCodeImporter {

    private static final Logger logger = Logger.getLogger(FSSourceImporter.class);

    private final String path;

    private String projectName;

    FSSourceImporter(String path) {
        this.path = path;
    }

    @Override
    public void setCredentials(String username, String password) {
        logger.debug("Nothing to do here!");
    }

    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public SourceCodeDirectory importSource() {
        Path path = Paths.get(this.path);
        Assert.isTrue(path.toFile().exists() && path.toFile().isDirectory(), "Path doesn't exist or is not directory");

        if (StringUtils.isEmpty(projectName)) {
            return new SourceCodeDirectory(path.getFileName().toString(), path.toAbsolutePath().toString());
        } else {
            return new SourceCodeDirectory(this.projectName, path.toAbsolutePath().toString());
        }
    }
}
