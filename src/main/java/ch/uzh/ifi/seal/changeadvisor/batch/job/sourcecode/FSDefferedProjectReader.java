package ch.uzh.ifi.seal.changeadvisor.batch.job.sourcecode;

import ch.uzh.ifi.seal.changeadvisor.parser.bean.ClassBean;
import ch.uzh.ifi.seal.changeadvisor.parser.bean.PackageBean;
import ch.uzh.ifi.seal.changeadvisor.source.SourceCodeDirectory;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;


/**
 * File System project reviewReader.
 * Created by alex on 15.07.2017.
 */
public class FSDefferedProjectReader implements FileSystemReader {

    private final FSProjectReader reader;

    public FSDefferedProjectReader(FSProjectReader reader) {
        this.reader = reader;
    }


    @BeforeStep
    public void setProjectRootPath(StepExecution stepExecution) {
        SourceCodeDirectory directory = (SourceCodeDirectory) stepExecution.getJobExecution().getExecutionContext().get("directory");
        reader.setProjectRootPath(directory.getPath());
    }

    /**
     * Sets whether this reviewReader should read the classes sorted or unsorted.
     * Sort order is defined by the {@link PackageBean}.
     *
     * @param sortedRead true iff reads should happens in order. False otherwise.
     * @see PackageBean#compareTo(PackageBean)
     */
    public void setSortedRead(boolean sortedRead) {
        reader.setSortedRead(sortedRead);
    }

    @Override
    public ClassBean read() throws Exception {
        return reader.read();
    }
}
