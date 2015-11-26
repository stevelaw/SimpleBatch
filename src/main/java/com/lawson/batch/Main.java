package com.lawson.batch;

import java.util.Date;

import com.lawson.batch.job.Job;
import com.lawson.batch.job.JobStream;
import com.lawson.batch.job.JobStreamRunner;
import com.lawson.batch.job.SerialJobStream;

public class Main {

	public static void main(String[] args) {
//		final JobStream rootJobStream = new SerialJobStream("1", new CronTrigger("0 07 12 * * ?"));
		final JobStream rootJobStream = new SerialJobStream("1");
//		final JobStream rootJobStream = new ParallelJobStream("1");

		final JobStream rootChild1 = new SerialJobStream("2");

		rootChild1.addJob(new Job("3") {
			@Override
			public void process(Date tick, Object data) {
				setJobSuccessful(null);
			}
		});

		rootChild1.addJob(new Job("4") {
			@Override
			public void process(Date tick, Object data) {
				setJobSuccessful(null);
			}
		});

		rootChild1.addJob(new Job("5") {
			@Override
			public void process(Date tick, Object data) {
				setJobSuccessful(null);
			}
		});

		final JobStream rootChild2 = new SerialJobStream("6");

		rootChild2.addJob(new Job("7") {
			@Override
			public void process(Date tick, Object data) {
				setJobSuccessful(null);
			}
		});

		rootChild2.addJob(new Job("8") {
			@Override
			public void process(Date tick, Object data) {
				setJobSuccessful(null);
			}
		});

		rootChild2.addJob(new Job("9") {
			@Override
			public void process(Date tick, Object data) {
				setJobSuccessful(null);
			}
		});

		// Add child job streams to root
		rootJobStream.addJob(rootChild1);
		rootChild1.addJob(rootChild2);

		// Start the runner
		final JobStreamRunner runnner = new JobStreamRunner(rootJobStream);
		runnner.start();
	}
}
