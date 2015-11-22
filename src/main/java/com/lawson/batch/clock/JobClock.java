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

	private Set<JobClockHandler> jobs = new CopyOnWriteArraySet<>();

	public void start() {
		this.scheduler.scheduleAtFixedRate(new Runnable() {
			public void run() {
				final ZonedDateTime zonedNow = ZonedDateTime.now(DEFAULT_ZONE_ID);
				final Date tick = Date.from(zonedNow.toInstant());

				postTick(tick);
			}
		}, 0, 100, TimeUnit.MILLISECONDS);
	}

	public void registerJob(final Job job) {
		this.jobs.add(job);
	}

	public boolean unregisterJob(final Job job) {
		final boolean isRemoved = this.jobs.remove(job);

		if (isRemoved) {
			LOGGER.info("Job " + job + " unregistered successfully");
		} else {
			LOGGER.info("Job " + job + " unregistered unsuccessful");
		}

		return isRemoved;
	}

	public void unregisterJobStream(final JobStream jobStream) {
		final List<Job> jobs = jobStream.getJobs();

		jobs.parallelStream().forEach(job -> JobClock.INSTANCE.unregisterJob(job));

		this.unregisterJob(jobStream);
	}

	private void postTick(final Date tick) {
		// Not sure if we want to shutdown when no jobs
		// if(this.jobs.size() == 0) {
		// this.scheduler.shutdown();
		// }

		this.jobs.parallelStream().forEach(job -> {
			dispatchers.execute(new Runnable() {
				@Override
				public void run() {
					job.onTick(tick);
				}
			});
		});
	}
}
