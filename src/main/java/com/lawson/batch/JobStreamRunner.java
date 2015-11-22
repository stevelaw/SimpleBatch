package com.lawson.batch;

import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.jobstream.JobStream;

public class JobStreamRunner {
	private final static Logger LOGGER = Logger.getLogger(JobStreamRunner.class.getName());
	
	private JobStream jobStream;

	public JobStreamRunner(JobStream jobStream, Level logLevel) {
		LOGGER.setLevel(logLevel);
		this.addLogHandlers(logLevel);
		
		this.jobStream = jobStream;
		
		JobClock.INSTANCE.registerJob(jobStream);
	}

	public JobStream getJobStream() {
		return jobStream;
	}

	public void start() {
		JobClock.INSTANCE.start();
	}
	
	private void addLogHandlers(final Level level) {
		final ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(level);
		
		LOGGER.addHandler(consoleHandler);
	}
}
