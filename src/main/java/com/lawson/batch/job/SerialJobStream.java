package com.lawson.batch.job;

import java.util.logging.Logger;

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

		if (this.numberOfJobs() == 0) {
			job.addDependency(this);
		} else {
			final Job prevJob = this.getJobs().get(this.numberOfJobs() - 1);
			job.addDependency(prevJob);
		}

		LOGGER.info("Ading job " + job + " with dependencies " + job.getDependencies());

		this.addJobInternal(job);
	}
}
