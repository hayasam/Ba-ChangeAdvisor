package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitSourceCodeImporter implements SourceCodeImporter {

    private static final Logger logger = Logger.getLogger(GitSourceCodeImporter.class);

    static final File IMPORTED_CODE_FOLDER = new File("imported_code");

    private static final Pattern PROJECT_NAME_GIT_PATTERN = Pattern.compile(".*/(\\w+).git");

    private final String path;

    private CredentialsProvider credentialsProvider;

    GitSourceCodeImporter(String path) {
        this.path = path;
    }

    @Override
    public void setCredentials(String username, String password) {
        username = username != null ? username : "";
        password = password != null ? password : "";
        credentialsProvider = new UsernamePasswordCredentialsProvider(username, password);
    }

    @Override
    public SourceCodeDirectory importSource() {
        final String REMOTE_URL = getURLFromPath();
        final String projectName = getProjectNameFromPath();
        final File projectPath = new File(IMPORTED_CODE_FOLDER.getName() + "/" + projectName);

        if (projectPath.exists()) {
            clearDirectory(projectPath);
        }

        logger.info(String.format("Cloning project %s from %s to %s", projectName, REMOTE_URL, projectPath.getPath()));
        try (Git result = Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(projectPath)
                .setCredentialsProvider(credentialsProvider)
                .call()) {

            logger.info(String.format("Having repository: %s", result.getRepository().getDirectory()));
            return SourceCodeDirectory.of(projectName, projectPath.getAbsolutePath(), REMOTE_URL);
        } catch (TransportException e) {
            throw new RuntimeException(String.format("No Credentials provided for repository or no repository found @ %s", REMOTE_URL), e);
        } catch (GitAPIException e) {
            logger.error(String.format("Failed to clone the repository @ %s.", REMOTE_URL), e);
            throw new RuntimeException(String.format("Failed to clone the repository @ %s.", REMOTE_URL), e);
        }
    }

    private void clearDirectory(File path) {
        try {
            FileUtils.deleteDirectory(path);
        } catch (IOException e) {
            logger.error("Failed to delete directory");
            throw new RuntimeException("Failed to delete directory", e);
        }
    }

    String getURLFromPath() {
        return path.replace("git://", "");
    }

    String getProjectNameFromPath() {
        Matcher matcher = PROJECT_NAME_GIT_PATTERN.matcher(path);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("No *.git pattern found");
    }
}
