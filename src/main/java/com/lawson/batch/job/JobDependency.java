package com.lawson.batch.job;

/**
 * Object representing a dependency on a job and the status code state required
 * for the dependency to be considered satisfied.
 */
public class JobDependency {
	final private Job job;
	final private JobStatusCode jobStatusCode;

	public JobDependency(Job job, JobStatusCode jobStatusCode) {
		this.job = job;
		this.jobStatusCode = jobStatusCode;
	}

	/**
	 * Returns true if the status code of the dependent job equals the expected
	 * status code, false otherwise.
	 * 
	 * @return True if the status code of the dependent job equals the expected
	 *         status code, false otherwise
	 */
	public Boolean isSatisfied() {
		return this.job.getStatusCode() == this.jobStatusCode;
	}

	public Job getJob() {
		return job;
	}

	public JobStatusCode getJobStatusCode() {
		return jobStatusCode;
	}
}
