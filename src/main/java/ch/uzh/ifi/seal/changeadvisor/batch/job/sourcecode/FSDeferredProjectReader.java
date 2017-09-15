package ch.uzh.ifi.seal.changeadvisor.batch.job.sourcecode;

import ch.uzh.ifi.seal.changeadvisor.source.SourceCodeDirectory;
import ch.uzh.ifi.seal.changeadvisor.source.parser.bean.ClassBean;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;


/**
 * Decorator for FSProjectReader.
 * When used as part of a batch job, it reads the directory to parse from the job context.
 * Specifically it looks for DIRECTORY_KEY in the context.
 * Created by alex on 15.07.2017.
 *
 * @see FileSystemReader
 * @see FSProjectReader
 */
public class FSDeferredProjectReader implements FileSystemReader {

    private static final String DIRECTORY_KEY = "directory";

    private final FileSystemReader reader;

    public FSDeferredProjectReader(FSProjectReader reader) {
        this.reader = reader;
    }

    @SuppressWarnings("unused")
    @BeforeStep
    public void setProjectRootPath(StepExecution stepExecution) {
        SourceCodeDirectory directory = getDirectoryFromStepExecutionContext(stepExecution);
        setProjectRootPath(directory.getPath());
    }

    @Override
    public void setProjectRootPath(String path) {
        reader.setProjectRootPath(path);
    }

    private SourceCodeDirectory getDirectoryFromStepExecutionContext(StepExecution stepExecution) {
        Object directory = stepExecution.getJobExecution().getExecutionContext().get(DIRECTORY_KEY);
        if (directory == null || !SourceCodeDirectory.class.isInstance(directory)) {
            throw new IllegalArgumentException(String.format("Couldn't find directory in Step Context. Found %s", directory));
        }
        return (SourceCodeDirectory) directory;
    }

    @Override
    public void setSortedRead(boolean sortedRead) {
        reader.setSortedRead(sortedRead);
    }

    @Override
    public ClassBean read() throws Exception {
        return reader.read();
    }
}
