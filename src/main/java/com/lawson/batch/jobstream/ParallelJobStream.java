package com.lawson.batch.jobstream;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.job.Job;

public class ParallelJobStream extends JobStream {
	
	public ParallelJobStream(String name) {
		super(name);
	}

	public void addJob(final Job job) {
		if (job == null) {
			throw new IllegalArgumentException("Job is null");
		}

		System.out.println("Ading job " + job);
		
		this.jobs.add(job);

		JobClock.INSTANCE.registerJob(job);
	}
}
