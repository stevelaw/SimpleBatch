package com.lawson.batch.jobstream;

import java.util.logging.Logger;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.job.Job;
import com.lawson.batch.trigger.Trigger;

public class SerialJobStream extends JobStream {
	private final static Logger LOGGER = Logger.getLogger(JobStream.class.getName());
	
	public SerialJobStream(String name) {
		super(name);
	}
	
	public SerialJobStream(String name, Trigger trigger) {
		super(name, trigger);
	}

	public void addJob(final Job job) {
		if (job == null) {
			throw new IllegalArgumentException("Job is null");
		}

		if (this.jobs.size() == 0) {
			job.setDependency(this);
		} else {
			final Job prevJob = this.jobs.get(this.jobs.size() -1);
			job.setDependency(prevJob);
		}
		
		LOGGER.info("Ading job " + job + " with dependencies " + job.getDependencies());
		
		this.jobs.add(job);

		JobClock.INSTANCE.register(job);
	}
}
