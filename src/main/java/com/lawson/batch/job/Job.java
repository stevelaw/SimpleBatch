package com.lawson.batch.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.clock.JobClockHandler;
import com.lawson.batch.util.JobStatusCode;
import com.lawson.batch.util.Stopwatch;

public abstract class Job implements JobClockHandler {
	private UUID id;
	private String name;
	private JobStatusCode statusCode = JobStatusCode.PENDING;
	private List<Job> dependencies;

	Stopwatch stopwatch;

	public Job(final String name) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.dependencies = new ArrayList<>();
	}

	public void setDependencies(final List<Job> dependencies) {
		this.dependencies = dependencies;
	}

	public void setDependency(final Job dependency) {
		this.dependencies = new ArrayList<>();
		this.dependencies.add(dependency);
	}

	public List<Job> getDependencies() {
		return dependencies;
	}

	public void preProcess(final Date tick, final Object data) {
	}

	public abstract void process(final Date tick, final Object data);

	public void postProcess(final Date tick, final Object data) {
	}

	@Override
	public void onTick(final Date tick) {
		if (this.isAlive() && this.isSatisfiedBy(tick) && this.dependenciesSatisfied()) {
			this.preProcess(tick, null);

			System.out.println("Job " + this + " started");

			stopwatch = new Stopwatch();

			// TODO: Get data from dependencies.
			this.process(tick, null);

			this.postProcess(tick, null);
		}
	}

	protected void setStatusCode(JobStatusCode statusCode) {
		this.statusCode = statusCode;

		final double elapsedTime = stopwatch.elapsedTime();

		System.out.println("Job " + this + " ended with status " + statusCode + " in " + elapsedTime + " ms");

		if (statusCode == JobStatusCode.SUCCESS || statusCode == JobStatusCode.FAILURE) {
			JobClock.INSTANCE.unregisterJob(this);
		}
	}

	public JobStatusCode getStatusCode() {
		return statusCode;
	}

	public UUID getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	/**
	 * Returns true if available to run (pending or running).
	 * 
	 * @return True if available to run (pending or running).
	 */
	protected boolean isAlive() {
		return this.getStatusCode() == JobStatusCode.PENDING || this.getStatusCode() == JobStatusCode.RUNNING;
	}

	/**
	 * The method can be overridden to give the job the opportunity to determine
	 * if it can run based on the date passed in.
	 * 
	 * @param tick
	 *            Current tick from the master clock.
	 * @return Returns true by default.
	 */
	protected Boolean isSatisfiedBy(final Date tick) {
		return true;
	}

	/**
	 * Checks all dependencies to determine if they are all successful.
	 * 
	 * @return True if all dependencies are successful.
	 */
	protected Boolean dependenciesSatisfied() {
		return this.dependencies == null
				|| this.dependencies.parallelStream().allMatch(job -> job.statusCode.equals(JobStatusCode.SUCCESS));
	}

	/**
	 * Returns true if the job is running.
	 * 
	 * @return True if the job is running, false otherwise.
	 */
	protected Boolean isRunning() {
		return this.getStatusCode() == JobStatusCode.RUNNING;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;

		result = prime * result + ((id == null) ? 0 : id.hashCode());

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj == null) {
			return false;
		}

		if (getClass() != obj.getClass()) {
			return false;
		}

		Job other = (Job) obj;

		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}

		return true;
	}

	@Override
	public String toString() {
		return "Job [name=" + name + "]";
	}
}
