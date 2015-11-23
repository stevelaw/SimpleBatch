package com.lawson.batch;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.clock.JobClockHandler;
import com.lawson.batch.exception.JobException;
import com.lawson.batch.jobstream.JobStream;
import com.lawson.batch.util.CronExpression;

public class JobStreamRunner implements JobClockHandler {
	private final static Logger LOGGER = Logger.getLogger(JobStreamRunner.class.getName());
	
	private CronExpression midnightCronExpression;
	
	private JobStream jobStream;

	public JobStreamRunner(JobStream jobStream, Level logLevel) {
		this.setupLogger(logLevel);
		this.setupMidnightCronExpression();
		
		this.jobStream = jobStream;
		
		JobClock.INSTANCE.register(this);
		JobClock.INSTANCE.register(jobStream);
	}

	public JobStream getJobStream() {
		return jobStream;
	}

	public void start() {
		JobClock.INSTANCE.start();
	}
	
	private void setupMidnightCronExpression() {
		try {
			this.midnightCronExpression = new CronExpression("0 0 0 * * ?");
		} catch (ParseException e) {
			throw new JobException("Error creating cron expression", e);
		}
	}
	
	private void setupLogger(final Level logLevel) {
		LOGGER.setLevel(logLevel);
		this.addLogHandlers(logLevel);
	}
	
	private void addLogHandlers(final Level level) {
		final ConsoleHandler consoleHandler = new ConsoleHandler();
		consoleHandler.setLevel(level);
		
		LOGGER.addHandler(consoleHandler);
	}

	@Override
	public void onTick(Date tick) {
		if (this.midnightCronExpression.isSatisfiedBy(tick)) {
			LOGGER.info("Midnight detected. Attempting to reset jobs.");
			
			if (this.jobStream.isAllSuccessful()) {
				this.jobStream.resetAll();
			}
		}
	}
}
