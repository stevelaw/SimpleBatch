package com.lawson.batch;

import java.text.ParseException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.clock.JobClockHandler;
import com.lawson.batch.exception.JobException;
import com.lawson.batch.jobstream.JobStream;
import com.lawson.batch.util.CronExpression;

public class JobStreamRunner implements JobClockHandler {
	private final static Logger LOGGER = Logger.getLogger(JobStreamRunner.class.getName());

	final private JobStream jobStream;
	final JobStreamRunnerConfig config;

	private CronExpression midnightCronExpression;

	public JobStreamRunner(final JobStream jobStream) {
		this(jobStream, new JobStreamRunnerConfig.Builder().build());
	}

	public JobStreamRunner(final JobStream jobStream, final JobStreamRunnerConfig config) {
		this.config = config;
		this.jobStream = jobStream;

		this.setupLogger(config.getLogLevel());
		this.setupMidnightCronExpression();

		JobClock.INSTANCE.configure(config);
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
		if (this.config.getLogHandlers() != null) {
			this.config.getLogHandlers().forEach(handler -> {
				handler.setLevel(level);
				LOGGER.addHandler(handler);
			});
		}
	}

	@Override
	public void onTick(final Date tick) {
		if (this.midnightCronExpression.isSatisfiedBy(tick)) {
			LOGGER.info("Midnight detected. Attempting to reset jobs.");

			if (this.jobStream.isAllSuccessful()) {
				this.jobStream.resetAll();
			}
		}
	}
}
