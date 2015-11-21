package com.lawson.batch;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.jobstream.JobStream;

public class JobStreamRunner {
	final static Logger logger = Logger.getLogger("jobStreamRunner");
	
	private JobStream jobStream;

	public JobStreamRunner(JobStream jobStream, Level logLevel) {
		logger.setLevel(logLevel);
		
		this.jobStream = jobStream;
		
		JobClock.INSTANCE.registerJob(jobStream);
	}

	public JobStream getJobStream() {
		return jobStream;
	}

	public void start(final JobStreamContext context) {
		JobClock.INSTANCE.start();
	}
}
