package com.lawson.batch.clock;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.lawson.batch.JobStreamRunnerConfig;
import com.lawson.batch.jobstream.JobStream;

public class JobClock {
	private final static Logger LOGGER = Logger.getLogger(JobStream.class.getName());

	final JobStreamRunnerConfig config;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private ScheduledExecutorService dispatchers;

	private ZoneId zoneId;

	private Set<JobClockHandler> observers = new CopyOnWriteArraySet<>();

	public JobClock(final JobStreamRunnerConfig config) {
		this.config = config;

		this.zoneId = config.getTimezoneZoneId();
		this.dispatchers = Executors.newScheduledThreadPool(config.getNumberDispatcherThreads());
	}

	public void start() {
		this.startTimer();
	}

	public void register(final JobClockHandler handler) {
		this.observers.add(handler);
	}

	public boolean unregister(final JobClockHandler handler) {
		final boolean isRemoved = this.observers.remove(handler);

		if (isRemoved) {
			LOGGER.info("Clock observer " + handler + " unregistered successfully");
		} else {
			LOGGER.info("Clock observer " + handler + " unregistered unsuccessful");
		}

		return isRemoved;
	}

	private void startTimer() {
		this.scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				final ZonedDateTime zonedNow = ZonedDateTime.now(zoneId);
				final Date tick = Date.from(zonedNow.toInstant());

				postTick(tick);
			}
		}, 0, this.config.getTimerTickIntervalMS(), TimeUnit.MILLISECONDS);
	}

	private void postTick(final Date tick) {
		this.observers.parallelStream().forEach(job -> {
			dispatchers.execute(new Runnable() {
				@Override
				public void run() {
					job.onTick(tick);
				}
			});
		});
	}
}
