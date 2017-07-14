package ch.uzh.ifi.seal.changeadvisor.parser;

import ch.uzh.ifi.seal.changeadvisor.parser.bean.PackageBean;
import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * Tests for FSProjectParser using the frostwire app.
 * Created by alexanderhofmann on 14.07.17.
 */
public class FSProjectParserTest {

    private static final String TEST_DIRECTORY = "test_files_parser/com.frostwire.android";

    private FSProjectParser projectParser = new FSProjectParser();

    @Test
    public void parseFrostwireRoot() throws Exception {
        final Path projectRoot = Paths.get(TEST_DIRECTORY);
        List<PackageBean> projectPackages = projectParser.parse(projectRoot);
        Assert.assertThat(projectPackages.size(), is(135));
    }

    @Test
    public void parseCommonRoot() throws Exception {
        final Path projectCommonRoot = Paths.get(TEST_DIRECTORY + "/common");
        List<PackageBean> commonPackages = projectParser.parse(projectCommonRoot);
        Collections.sort(commonPackages);
        Assert.assertThat(commonPackages.size(), is(32));
    }

    @Test
    public void parseAndroidRoot() throws Exception {
        final Path projectAndroidRoot = Paths.get(TEST_DIRECTORY + "/android");
        List<PackageBean> androidPackages = projectParser.parse(projectAndroidRoot);
        Assert.assertThat(androidPackages.size(), is(50));
    }

    @Test
    public void parseAndroidApolloRoot() throws Exception {
        final Path projectAndroidApolloRoot = Paths.get(TEST_DIRECTORY + "/android/apollo");
        List<PackageBean> androidApolloPackages = projectParser.parse(projectAndroidApolloRoot);
        Assert.assertThat(androidApolloPackages.size(), is(22));
    }

    @Test
    public void parseAndroidTestsRoot() throws Exception {
        final Path projectAndroidTestsRoot = Paths.get(TEST_DIRECTORY + "/android/tests");
        List<PackageBean> androidTestspackages = projectParser.parse(projectAndroidTestsRoot);
        Assert.assertThat(androidTestspackages.size(), is(6));
    }

    @Test
    public void parseDesktopRoot() throws Exception {
        final Path projectDesktopRoot = Paths.get(TEST_DIRECTORY + "/desktop");
        List<PackageBean> desktopPackages = projectParser.parse(projectDesktopRoot);
        Assert.assertThat(desktopPackages.size(), is(55));
    }

    @Test
    public void parseAndGetPublicCorpusFrostwireTest() throws Exception {
        final Path root = Paths.get(TEST_DIRECTORY + "/common/src/main/java/com/frostwire/bittorrent");
        List<PackageBean> packages = projectParser.parse(root);
        Assert.assertThat(packages.size(), is(1));

        PackageBean packageBean = packages.get(0);
        Assert.assertThat(packageBean.getCompilationUnits().size(), is(11));
    }
}
