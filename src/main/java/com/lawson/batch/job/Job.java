package com.lawson.batch.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.lawson.batch.JobStreamContext;
import com.lawson.batch.clock.JobClock;
import com.lawson.batch.clock.JobClockHandler;
import com.lawson.batch.util.JobStatusCode;

public abstract class Job implements JobClockHandler {
	private Long id;
	private String name;
	private JobStatusCode statusCode = JobStatusCode.PENDING;
	private List<Job> dependencies;

	public Job(final String name) {
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

	public abstract void process(final JobStreamContext context, final Date tick, final Object data);

	@Override
	public void onTick(final Date tick) {
		if (this.isAlive() && this.isSatisfiedBy(tick) && this.dependenciesSatisfied()) {
			System.out.println("Job " + this + " started");
			
			// TODO: keep track how long the job takes
			
			// TODO: Have clock pass context, and get data from dependencies.
			this.process(null, tick, null);
		}
	}

	protected void setStatusCode(JobStatusCode statusCode) {
		this.statusCode = statusCode;

		System.out.println("Job " + this + " ended with success? " + (statusCode == JobStatusCode.SUCCESS));
		
		if (statusCode == JobStatusCode.SUCCESS || statusCode == JobStatusCode.FAILURE) {
			JobClock.INSTANCE.unregisterJob(this);
		}
	}

	public JobStatusCode getStatusCode() {
		return statusCode;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	// ----------------
	// Private Methods
	// ----------------

	/*
	 * True if available to run (pending or running).
	 */
	protected boolean isAlive() {
		return this.getStatusCode() == JobStatusCode.PENDING || this.getStatusCode() == JobStatusCode.RUNNING;
	}

	protected Boolean isSatisfiedBy(final Date tick) {
		return true;
	}

	protected Boolean dependenciesSatisfied() {
		return this.dependencies == null || 
				this.dependencies.size() == 0 ||
				this.dependencies.parallelStream().allMatch(job -> job.statusCode.equals(JobStatusCode.SUCCESS));
	}
	
	protected Boolean isRunning() {
		return this.getStatusCode() == JobStatusCode.RUNNING;
	}
	
	@Override
	public String toString() {
		return "Job [name=" + name + "]";
	}
}
