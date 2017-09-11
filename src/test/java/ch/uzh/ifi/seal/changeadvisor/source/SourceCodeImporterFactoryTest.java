package ch.uzh.ifi.seal.changeadvisor.source;

import ch.uzh.ifi.seal.changeadvisor.web.SourceCodeDirectoryDto;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.core.Is.is;

public class SourceCodeImporterFactoryTest {

    @Test
    public void getImporterFS() throws Exception {
        String fileSystemPath = "file://Users/hoal/Document";

        SourceCodeImporter importer = SourceCodeImporterFactory.getImporter(fileSystemPath);
        Assert.assertThat(importer, is(instanceOf(FSSourceImporter.class)));
        Assert.assertThat(importer, not(instanceOf(GitSourceCodeImporter.class)));
    }

    @Test
    public void getImporterGit() throws Exception {
        String gitPath = "git://https://github.com/a-a-hofmann/SoftwareProject.git";

        SourceCodeImporter importer = SourceCodeImporterFactory.getImporter(gitPath);
        Assert.assertThat(importer, not(instanceOf(FSSourceImporter.class)));
        Assert.assertThat(importer, is(instanceOf(GitSourceCodeImporter.class)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void getImporterEmpty() throws Exception {
        SourceCodeImporterFactory.getImporter((SourceCodeDirectoryDto) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getImporterEmpty2() throws Exception {
        SourceCodeImporterFactory.getImporter((String) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void getImporterWrong() throws Exception {
        SourceCodeImporterFactory.getImporter("asdfasdf");
    }

    @Test(expected = IllegalArgumentException.class)
    public void getImporterWrong2() throws Exception {
        SourceCodeImporterFactory.getImporter(new SourceCodeDirectoryDto("adfdsf"));
    }
}