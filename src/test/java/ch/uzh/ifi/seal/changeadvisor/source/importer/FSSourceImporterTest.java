package ch.uzh.ifi.seal.changeadvisor.source.importer;

import ch.uzh.ifi.seal.changeadvisor.source.model.SourceCodeDirectory;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

public class FSSourceImporterTest {

    private static final String IMPORTED_CODE_FOLDER = "imported_code";

    @Test
    public void importSource() throws Exception {
        FSSourceImporter importer = new FSSourceImporter(IMPORTED_CODE_FOLDER);
        SourceCodeDirectory directory = importer.importSource();
        Assert.assertThat(directory.getProjectName(), is(IMPORTED_CODE_FOLDER));

        if (SystemUtils.IS_OS_WINDOWS) {
            Assert.assertThat(directory.getPath(), is("C:\\Users\\alex\\Documents\\Java\\ChangeAdvisor\\imported_code"));
        } else {
            Assert.assertThat(directory.getPath(), is("/Users/alexanderhofmann/Dropbox/UZH/Bsc/Ba/changeadvisor/imported_code"));
        }
    }

}