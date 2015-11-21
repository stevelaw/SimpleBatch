package com.lawson.batch.jobstream;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import com.lawson.batch.JobStreamContext;
import com.lawson.batch.exception.JobException;
import com.lawson.batch.job.Job;
import com.lawson.batch.util.JobStatusCode;

public abstract class JobStream extends Job {
	private final static Logger log = Logger.getLogger("jobStreamRunner.jobStream");

	protected List<Job> jobs = new ArrayList<Job>();

	// -------------
	// Constructors
	// -------------

	public JobStream(final String name) {
		super(name);
	}

	// ---------------
	// Public Methods
	// ---------------

	@Override
	public void process(final JobStreamContext context, final Date tick, final Object data) {
		if (this.jobs.size() == 0) {
			throw new JobException("No jobs added to jobstream");
		}

		System.out.println("JobStream " + this.getName() + " starting");

		setStatusCode(JobStatusCode.SUCCESS);
	}

	public abstract void addJob(final Job job);

	public List<Job> getJobs() {
		return jobs;
	}

	// ----------------
	// Private Methods
	// ----------------

	/*
	 * If all jobs were successful, then job stream is successful, otherwise set
	 * to failure.
	 */
	private void setCollectiveJobStatus(final List<Job> jobs) {
		boolean allSuccessful = jobs.stream().allMatch(job -> job.getStatusCode() == JobStatusCode.SUCCESS);

		if (allSuccessful) {
			setStatusCode(JobStatusCode.SUCCESS);
		} else {
			setStatusCode(JobStatusCode.FAILURE);
		}
	}
}
