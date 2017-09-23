package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.web.dto.SourceCodeDirectoryDto;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class FSSourceImporterTest {

    private static final String IMPORTED_CODE_FOLDER = "imported_code";

    private static final String PROJECT_NAME = "test";

    @Test
    public void importSource() throws Exception {
        SourceCodeDirectoryDto dto = new SourceCodeDirectoryDto(IMPORTED_CODE_FOLDER, PROJECT_NAME);
        FSSourceImporter importer = new FSSourceImporter(dto);
        SourceCodeDirectory directory = importer.importSource();

        Assert.assertThat(directory.getProjectName(), is(PROJECT_NAME));
        if (SystemUtils.IS_OS_WINDOWS) {
            Assert.assertThat(directory.getPath(), is("C:\\Users\\alex\\Documents\\Java\\ChangeAdvisor\\imported_code"));
        } else {
            Assert.assertThat(directory.getPath(), is("/Users/alexanderhofmann/Dropbox/UZH/Bsc/Ba/changeadvisor/imported_code"));
        }
    }

    @Test
    public void importSourceDefaultProjectName() throws Exception {
        SourceCodeDirectoryDto dto = new SourceCodeDirectoryDto(IMPORTED_CODE_FOLDER, "");
        FSSourceImporter importer = new FSSourceImporter(dto);
        SourceCodeDirectory directory = importer.importSource();

        Assert.assertThat(directory.getProjectName(), is(IMPORTED_CODE_FOLDER));
        if (SystemUtils.IS_OS_WINDOWS) {
            Assert.assertThat(directory.getPath(), is("C:\\Users\\alex\\Documents\\Java\\ChangeAdvisor\\imported_code"));
        } else {
            Assert.assertThat(directory.getPath(), is("/Users/alexanderhofmann/Dropbox/UZH/Bsc/Ba/changeadvisor/imported_code"));
        }
    }
}