package com.lawson.batch;

import java.util.Date;
import java.util.logging.Level;

import com.lawson.batch.job.Job;
import com.lawson.batch.jobstream.JobStream;
import com.lawson.batch.jobstream.SerialJobStream;
import com.lawson.batch.util.JobStatusCode;

public class Main {

	public static void main(String[] args) {
		final JobStream rootJobStream = new SerialJobStream("1");
		final JobStreamRunner runnner = new JobStreamRunner(rootJobStream, Level.FINER);

		final JobStream rootChild1 = new SerialJobStream("2");

		rootChild1.addJob(new Job("3") {
			@Override
			public void process(Date tick, Object data) {
				setStatusCode(JobStatusCode.SUCCESS);
			}
		});

		rootChild1.addJob(new Job("4") {
			@Override
			public void process(Date tick, Object data) {
				setStatusCode(JobStatusCode.SUCCESS);
			}
		});

		rootChild1.addJob(new Job("5") {
			@Override
			public void process(Date tick, Object data) {
				setStatusCode(JobStatusCode.SUCCESS);
			}
		});

		final JobStream rootChild2 = new SerialJobStream("6");

		rootChild2.addJob(new Job("7") {
			@Override
			public void process(Date tick, Object data) {
				setStatusCode(JobStatusCode.SUCCESS);
			}
		});

		rootChild2.addJob(new Job("8") {
			@Override
			public void process(Date tick, Object data) {
				setStatusCode(JobStatusCode.SUCCESS);
			}
		});

		rootChild2.addJob(new Job("9") {
			@Override
			public void process(Date tick, Object data) {
				setStatusCode(JobStatusCode.SUCCESS);
			}
		});

		// Add child job streams to root
		rootJobStream.addJob(rootChild1);
		rootJobStream.addJob(rootChild2);

		// Start the runner
		runnner.start();
	}
}
