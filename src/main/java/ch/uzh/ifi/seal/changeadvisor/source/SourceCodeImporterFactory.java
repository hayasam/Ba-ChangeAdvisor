package ch.uzh.ifi.seal.changeadvisor.source;

import org.apache.commons.lang3.StringUtils;

public class SourceCodeImporterFactory {

    public static SourceCodeImporter getImporter(String path) {
        if (StringUtils.isEmpty(path)) {
            throw new IllegalArgumentException("Path to source code may not be empty");
        }

        if (path.startsWith("file://")) {
            return new FSSourceImporter(path);
        }

        if (path.startsWith("git://")) {
            return new GitSourceCodeImporter(path);
        }

        throw new IllegalArgumentException(String.format("Couldn't instantiate Source code importer. Can't determine path for %s", path));
    }
}
