package com.lawson.batch.jobstream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.lawson.batch.exception.JobException;
import com.lawson.batch.job.Job;
import com.lawson.batch.trigger.Trigger;
import com.lawson.batch.util.JobStatusCode;

public abstract class JobStream extends Job {
	private final static Logger LOGGER = Logger.getLogger(JobStream.class.getName());

	protected List<Job> jobs = new ArrayList<Job>();

	// -------------
	// Constructors
	// -------------

	public JobStream(final String name) {
		super(name);
	}

	public JobStream(String name, Trigger trigger) {
		super(name, trigger);
	}

	// ---------------
	// Public Methods
	// ---------------

	@Override
	public void process(final Date tick, final Object data) {
		if (this.jobs.size() == 0) {
			throw new JobException("No jobs added to jobstream");
		}

		setStatusCode(JobStatusCode.SUCCESS);
	}

	public abstract void addJob(final Job job);

	public List<Job> getJobs() {
		return jobs;
	}

	/**
	 * Returns true if all non-repeatable triggered jobs are successful.
	 * 
	 * @return True if all non-repeatable triggered jobs are successful.
	 */
	public Boolean isAllSuccessful() {
		return this.jobs.parallelStream().filter(job -> !job.getTrigger().isRepeatable())
				.allMatch(job -> job.getStatusCode().equals(JobStatusCode.SUCCESS));
	}

	public void resetAll() {
		this.jobs.parallelStream().forEach(job -> job.setStatusCode(JobStatusCode.PENDING));
	}
}
