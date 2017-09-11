package ch.uzh.ifi.seal.changeadvisor.source;

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
        Assert.assertThat(directory.getPath(), is("/Users/alexanderhofmann/Dropbox/UZH/Bsc/Ba/changeadvisor/imported_code"));
    }

}