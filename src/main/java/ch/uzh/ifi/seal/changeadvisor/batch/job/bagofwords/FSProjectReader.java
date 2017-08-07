package ch.uzh.ifi.seal.changeadvisor.batch.job.bagofwords;

import ch.uzh.ifi.seal.changeadvisor.parser.FSProjectParser;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.ClassBean;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.PackageBean;
import org.springframework.batch.item.ItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * File System project reviewReader.
 * Created by alex on 15.07.2017.
 */
public class FSProjectReader implements ItemReader<ClassBean> {

    private final FSProjectParser projectParser;

    private Path projectRootPath;

    private Iterator<PackageBean> packageIterator;

    private Iterator<ClassBean> classIterator;

    private List<PackageBean> packages;

    private boolean isSortedRead;

    @Autowired
    public FSProjectReader(FSProjectParser projectParser) {
        this.projectParser = projectParser;
        this.isSortedRead = false;
    }

    public void setProjectRootPath(String projectRoot) {
        Assert.notNull(projectRoot, "Project root to parse must not be null.");
        this.projectRootPath = Paths.get(projectRoot);
        Assert.isTrue(projectRootPath.toFile().exists(), String.format("Path [%s] does not exist.", projectRootPath));
        Assert.isTrue(projectRootPath.toFile().isDirectory(),
                String.format("Path [%s] is not a directory. Can't be Project root.", projectRootPath));
    }

    /**
     * Sets whether this reviewReader should read the classes sorted or unsorted.
     * Sort order is defined by the {@link PackageBean}.
     *
     * @param sortedRead true iff reads should happens in order. False otherwise.
     * @see PackageBean#compareTo(PackageBean)
     */
    public void setSortedRead(boolean sortedRead) {
        isSortedRead = sortedRead;
    }

    @Override
    public ClassBean read() throws Exception {
        if (hasNotParsedYet()) {
            Assert.notNull(projectRootPath, "Must set a path in config before running batch job.");
            packages = parse();
            packageIterator = packages.iterator();
        }
        if (needsNewClassIterator()) {
            classIterator = packageIterator.next().classIterator();
        }

        return classIterator.hasNext() ? classIterator.next() : null;
    }

    private List<PackageBean> parse() {
        List<PackageBean> projectPackages = projectParser.parse(projectRootPath, false);

        if (isSortedRead) {
            Collections.sort(projectPackages);
        }
        return projectPackages;
    }

    private boolean hasNotParsedYet() {
        return packages == null;
    }

    private boolean needsNewClassIterator() {
        return hasNoNextClass() && hasNextPackage();
    }

    private boolean hasNoNextClass() {
        return classIterator == null || !classIterator.hasNext();
    }

    private boolean hasNextPackage() {
        return packageIterator.hasNext();
    }
}
