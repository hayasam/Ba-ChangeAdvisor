package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.apache.commons.lang3.StringUtils;

public class SourceCodeImporterFactory {

    /**
     * Gets the appropriate source code importer for the given path.
     *
     * @param path path to source code.
     *             Can either be a 'file://' path or a 'git://' path.
     * @return Source code importer
     * @see SourceCodeImporter
     * @see FSSourceImporter
     * @see GitSourceCodeImporter
     */
    public static SourceCodeImporter getImporter(String path) {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Path to source code may not be empty");
        }

        if (isFileSystemPath(path)) {
            return new FSSourceImporter(path);
        }

        if (isGitPath(path)) {
            return new GitSourceCodeImporter(path);
        }

        throw new IllegalArgumentException(String.format("Couldn't instantiate Source code importer. Can't determine path for %s", path));
    }

    /**
     * Gets the appropriate source code importer for the given path.
     * Additionally sets the credentials if provided in the dto.
     *
     * @param dto {@link SourceCodeDirectoryDto}
     * @return Source code importer
     * @see SourceCodeImporter
     * @see FSSourceImporter
     * @see GitSourceCodeImporter
     */
    public static SourceCodeImporter getImporter(SourceCodeDirectoryDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Source code directory dto may not be null");
        }
        SourceCodeImporter importer = getImporter(dto.getPath());
        importer.setCredentials(dto.getUsername(), dto.getPassword());
        return importer;
    }

    private static boolean isFileSystemPath(String path) {
        return path.startsWith("file://");
    }

    private static boolean isGitPath(String path) {
        return (path.startsWith("git://") || path.startsWith("http")) && path.endsWith(".git");
    }
}
