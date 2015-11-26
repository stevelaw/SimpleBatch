package com.lawson.batch;

import static com.jayway.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.hamcrest.Matchers.is;
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
import com.lawson.batch.job.SerialJobStream;

public class SerialJobStreamTests {

	@Test
	public void doJobsRunSerially() {
		final JobStreamRunnerConfig config = new JobStreamRunnerConfig.Builder().timerTickIntervalMS(1).build();

		final JobStream jobStream = new SerialJobStream("Job Stream");

		final List<String> completionOrder = new ArrayList<>();
		final List<String> expectedOrder = new ArrayList<>();
		IntStream.range(0, 10).forEach(num -> {
			final String jobName = "Job " + num;

			// We're going to compare the actual job execution order with the
			// expected order list.
			expectedOrder.add(jobName);

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

		await().atMost(1, SECONDS).until(completionOrder::size, is(10));

		assertTrue(completionOrder.equals(expectedOrder));
	}

}
