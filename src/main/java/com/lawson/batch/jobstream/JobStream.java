package com.lawson.batch.jobstream;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.exception.JobException;
import com.lawson.batch.job.Job;
import com.lawson.batch.trigger.Trigger;
import com.lawson.batch.util.JobStatusCode;

public abstract class JobStream extends Job {
	private final static Logger LOGGER = Logger.getLogger(JobStream.class.getName());

	private List<Job> jobs = new ArrayList<Job>();

	public JobStream(final String name) {
		super(name);
	}

	public JobStream(String name, Trigger trigger) {
		super(name, trigger);
	}

	@Override
	public void process(final Date tick, final Object data) {
		if (this.jobs.size() == 0) {
			throw new JobException("No jobs added to jobstream");
		}

		setStatusAndData(JobStatusCode.SUCCESS, null);
	}

	public abstract void addJob(final Job job);

	/**
	 * Add job to internal data structure.
	 * 
	 * @param job
	 *            Job to add
	 */
	protected void addJobInternal(final Job job) {
		this.jobs.add(job);
	}

	/**
	 * Returns unmodifiable list of jobs.
	 * 
	 * @return unmodifiable list of jobs
	 */
	protected List<Job> getJobs() {
		return Collections.unmodifiableList(this.jobs);
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

	/**
	 * Reset the job state of all jobs back to the
	 * <code>JobStatusCode.PENDING</code> state, and reset the data.
	 */
	public void resetAll() {
		this.jobs.parallelStream().forEach(job -> job.setStatusAndData(JobStatusCode.PENDING, null));
	}

	/**
	 * To be provided by an ancestor. It is our responsibility to set upon any
	 * descendants.
	 */
	public void setJobClock(JobClock jobClock) {
		this.getJobs().stream().forEach(job -> {
			job.setJobClock(jobClock);
			jobClock.register(job);
		});
	}
}
