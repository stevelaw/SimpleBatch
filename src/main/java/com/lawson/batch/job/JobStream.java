package com.lawson.batch.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.exception.JobException;
import com.lawson.batch.job.snapshot.JobSnapshotVisitor;
import com.lawson.batch.trigger.Trigger;

public abstract class JobStream extends Job {
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

		this.setStatusAndData(JobStatusCode.RUNNING, null);
	}

	public abstract void addJob(final Job job);

	/**
	 * Add job to internal data structure.
	 * 
	 * @param job
	 *            Job to add
	 */
	void addJobInternal(final Job job) {
		this.jobs.add(job);
	}

	/**
	 * Returns unmodifiable list of jobs.
	 * 
	 * @return unmodifiable list of jobs
	 */
	public List<Job> getJobs() {
		return Collections.unmodifiableList(this.jobs);
	}

	Integer numberOfJobs() {
		return this.jobs.size();
	}

	@Override
	public synchronized void onTick(Date tick) {
		// Check for status code changes based on descendant statuses. If any
		// descendants failed, the status is set to failed. If all are
		// successful, the status is set to success.
		if (this.isAnyFailed()) {
			this.setStatusAndData(JobStatusCode.FAILURE, null);
		} else if (this.isAllSuccessful()) {
			this.setStatusAndData(JobStatusCode.SUCCESS, null);
		}
	}

	/**
	 * Returns true if all non-repeatable triggered jobs are running.
	 * 
	 * @return True if all non-repeatable triggered jobs are running.
	 */
	Boolean isAnyRunningSuccessful() {
		return this.jobs.parallelStream().filter(job -> !job.getTrigger().isRepeatable())
				.allMatch(job -> job.getStatusCode().equals(JobStatusCode.RUNNING));
	}

	/**
	 * Returns true if all non-repeatable triggered jobs are successful.
	 * 
	 * @return True if all non-repeatable triggered jobs are successful.
	 */
	Boolean isAllSuccessful() {
		return this.jobs.parallelStream().filter(job -> !job.getTrigger().isRepeatable())
				.allMatch(job -> job.getStatusCode().equals(JobStatusCode.SUCCESS));
	}

	/**
	 * Returns true if any non-repeatable triggered jobs failed.
	 * 
	 * @return True if any non-repeatable triggered jobs failed.
	 */
	Boolean isAnyFailed() {
		return this.jobs.parallelStream().filter(job -> !job.getTrigger().isRepeatable())
				.anyMatch(job -> job.getStatusCode().equals(JobStatusCode.FAILURE));
	}

	/**
	 * Reset the job state of all jobs back to the
	 * <code>JobStatusCode.PENDING</code> state, and reset the data.
	 */
	void resetAll() {
		this.setStatusAndData(JobStatusCode.PENDING, null);
		this.jobs.parallelStream().forEach(job -> job.setStatusAndData(JobStatusCode.PENDING, null));
	}

	/**
	 * To be provided by an ancestor. It is our responsibility to set upon any
	 * descendants.
	 */
	void setJobClock(JobClock jobClock) {
		this.getJobs().stream().forEach(job -> {
			job.setJobClock(jobClock);
			jobClock.register(job);
		});
	}

	/**
	 * Provides ability to pass in visitor to allow job and job stream to create
	 * a snapshot distinctly.
	 * 
	 * @param visitor
	 *            Visitor interface.
	 */
	public Object snapshot(final JobSnapshotVisitor visitor) {
		return visitor.snapshot(this);
	}
}
