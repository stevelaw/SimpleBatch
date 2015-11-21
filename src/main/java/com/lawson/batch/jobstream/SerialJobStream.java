package com.lawson.batch.jobstream;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.job.Job;

public class SerialJobStream extends JobStream {
	
	public SerialJobStream(String name) {
		super(name);
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
		
		System.out.println("Ading job " + job + " with dependencies " + job.getDependencies());
		
		this.jobs.add(job);

		JobClock.INSTANCE.registerJob(job);
	}
}
