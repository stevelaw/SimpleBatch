package com.lawson.batch;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import com.lawson.batch.job.Job;
import com.lawson.batch.job.JobStream;
import com.lawson.batch.job.JobStreamRunner;
import com.lawson.batch.job.JobStreamRunnerConfig;
import com.lawson.batch.job.ParallelJobStream;

public class ParallelJobStreamTests {
	private static final Integer NUMBER_OF_JOBS = 100;

	@Test
	public void doJobsRunInParallel() {
		final JobStreamRunnerConfig config = new JobStreamRunnerConfig.Builder().timerTickIntervalMS(1).build();

		final JobStream jobStream = new ParallelJobStream("Job Stream");

		final List<String> completionOrder = new ArrayList<>();
		final List<String> addOrder = new ArrayList<>();
		IntStream.range(0, NUMBER_OF_JOBS).forEach(num -> {
			final String jobName = "Job " + num;

			// We're going to compare the actual job execution order with the
			// expected order list.
			addOrder.add(jobName);

			jobStream.addJob(new Job(jobName) {
				@Override
				public void process(Date tick, Object data) {
					completionOrder.add(getName());
					setJobSuccessful(null);
				}
			});
		});

		final JobStreamRunner runner = new JobStreamRunner(jobStream, config);
		runner.start();

		await().atMost(5, SECONDS).until(completionOrder::size, is(NUMBER_OF_JOBS));

		assertEquals(NUMBER_OF_JOBS.intValue(), completionOrder.size());
		
		// Can technically pass, but unlikely.
		assertTrue(!completionOrder.equals(addOrder));
	}

}
