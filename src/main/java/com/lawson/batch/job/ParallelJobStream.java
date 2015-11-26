package com.lawson.batch.job;

import java.util.logging.Logger;

public class ParallelJobStream extends JobStream {
	private final static Logger LOGGER = Logger.getLogger(JobStream.class.getName());
	
	public ParallelJobStream(String name) {
		super(name);
	}

	public void addJob(final Job job) {
		if (job == null) {
			throw new IllegalArgumentException("Job is null");
		}

		LOGGER.info("Ading job " + job);
		
		this.addJobInternal(job);
	}
}
