package ch.uzh.ifi.seal.changeadvisor.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;

import static org.mockito.Mockito.verify;

public class JobServiceTest {

    @Mock
    private JobLauncher launcher;

    @InjectMocks
    private JobService service;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void run() throws Exception {
        Job job = Mockito.mock(Job.class);
        JobParameters parameters = service.parametersWithCurrentTimestamp();
        service.run(job, parameters);
        verify(launcher).run(job, parameters);
    }

    @Test(expected = IllegalArgumentException.class)
    public void runJobNull() throws Exception {
        service.run(null, service.parametersWithCurrentTimestamp());
    }

    @Test(expected = IllegalArgumentException.class)
    public void runParamsNull() throws Exception {
        Job job = Mockito.mock(Job.class);
        service.run(job, null);
    }
}