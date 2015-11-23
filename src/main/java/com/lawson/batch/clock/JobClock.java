package com.lawson.batch.clock;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import com.lawson.batch.job.Job;
import com.lawson.batch.jobstream.JobStream;

public enum JobClock {
	INSTANCE;
	
	private final static Logger LOGGER = Logger.getLogger(JobStream.class.getName()); 

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	private final ScheduledExecutorService dispatchers = Executors.newScheduledThreadPool(10);

	private static final ZoneId DEFAULT_ZONE_ID = ZoneId.of("America/New_York");

	private Set<JobClockHandler> observers = new CopyOnWriteArraySet<>();

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

	public void unregisterJobStream(final JobStream jobStream) {
		final List<Job> jobs = jobStream.getJobs();

		jobs.parallelStream().forEach(job -> JobClock.INSTANCE.unregister(job));

		this.unregister(jobStream);
	}

	private void startTimer() {
		this.scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				final ZonedDateTime zonedNow = ZonedDateTime.now(DEFAULT_ZONE_ID);
				final Date tick = Date.from(zonedNow.toInstant());

				postTick(tick);
			}
		}, 0, 1000, TimeUnit.MILLISECONDS);
	}
	
	private void postTick(final Date tick) {
		// Not sure if we want to shutdown when no jobs
		// if(this.jobs.size() == 0) {
		// this.scheduler.shutdown();
		// }

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
