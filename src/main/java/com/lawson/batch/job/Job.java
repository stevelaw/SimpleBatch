package com.lawson.batch.job;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.clock.JobClockHandler;
import com.lawson.batch.job.snapshot.JobSnapshotVisitor;
import com.lawson.batch.trigger.DefaultTrigger;
import com.lawson.batch.trigger.Trigger;
import com.lawson.batch.util.Stopwatch;

public abstract class Job implements JobClockHandler {
	private final static Logger LOGGER = Logger.getLogger(Job.class.getName());

	final private UUID id;
	final private String name;
	final private List<JobDependency> dependencies = new ArrayList<>();
	final private Trigger trigger;
	private volatile JobStatusCode statusCode = JobStatusCode.PENDING;
	private Object data;
	private JobClock jobClock;
	Stopwatch stopwatch;

	public Job(final String name) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.trigger = new DefaultTrigger();
	}

	public Job(final String name, final Trigger trigger) {
		this.id = UUID.randomUUID();
		this.name = name;

		if (trigger == null) {
			this.trigger = new DefaultTrigger();
		} else {
			this.trigger = trigger;
		}
	}

	public void addDependency(final JobDependency jobDependency) {
		this.dependencies.add(jobDependency);
	}

	public void addDependencies(final List<JobDependency> dependencies) {
		this.dependencies.addAll(dependencies);
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public Object getData() {
		return data;
	}

	public void preProcess(final Date tick, final Object data) {
	}

	public abstract void process(final Date tick, final Object data);

	public void postProcess(final Date tick, final Object data) {
	}

	/**
	 * Should not be called directly, and should only be called by clock.
	 */
	@Override
	public synchronized void onTick(final Date tick) {
		// The job is available to run (pending), the trigger condition is met,
		// and all dependencies are satisfied.
		if (this.isAvailableToRun() && this.trigger.isSatisfiedBy(tick) && this.dependenciesSatisfied()) {
			// We're running, so update status code
			this.statusCode = JobStatusCode.RUNNING;

			// Get dependency data to pass onto new job
			final Map<String, Object> jobDataByName = this.getJobDependencyDataByJobName();

			// Allow jobs to perform custom logic prior running.
			this.preProcess(tick, jobDataByName);

			LOGGER.info("Job " + this + " started");

			stopwatch = new Stopwatch();

			// Process the job logic
			this.process(tick, jobDataByName);

			// Allow jobs to perform custom logic after running.
			this.postProcess(tick, jobDataByName);
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

	public void setJobSuccessful(final Object data) {
		this.setStatusAndData(JobStatusCode.SUCCESS, data);
	}

	public void setJobFailure(final Object data) {
		this.setStatusAndData(JobStatusCode.FAILURE, data);
	}

	void setStatusAndData(final JobStatusCode statusCode, final Object data) {
		if (this.statusCode == statusCode) {
			return;
		}

		this.statusCode = statusCode;
		this.data = data;

		final double elapsedTime = stopwatch.elapsedTime();

		LOGGER.info("Job " + this + " completed with status " + statusCode + " in " + elapsedTime + " ms");

		// If the job is repeatable then re-set the status code to pending.
		if (this.trigger.isRepeatable()) {
			this.statusCode = JobStatusCode.PENDING;
		} else if ((statusCode == JobStatusCode.SUCCESS || statusCode == JobStatusCode.FAILURE)) {
			this.jobClock.unregister(this);
		}
	}

	void setJobClock(JobClock jobClock) {
		this.jobClock = jobClock;
	}

	/**
	 * Returns unmodifiable list of dependencies.
	 * 
	 * @return unmodifiable list of dependencies
	 */
	protected List<JobDependency> getDependencies() {
		return Collections.unmodifiableList(this.dependencies);
	}

	protected Optional<JobDependency> getDependencyByName(final String jobName) {
		return this.dependencies.stream().filter(dependency -> dependency.getJob().getName().equals(jobName))
				.findFirst();
	}

	/**
	 * Returns unmodifiable map of jobs to data.
	 * 
	 * @return unmodifiable map of jobs to data
	 */
	protected Map<String, Object> getJobDependencyDataByJobName() {
		// Collect Map of data from dependencies to pass onto next job
		final Map<String, Object> jobDataByName = new HashMap<>(this.dependencies.size());

		this.dependencies
				.forEach(dependency -> jobDataByName.put(dependency.getJob().getName(), dependency.getJob().getData()));

		return Collections.unmodifiableMap(jobDataByName);
	}

	/**
	 * Returns true if available to run (pending).
	 * 
	 * @return True if available to run (pending).
	 */
	private boolean isAvailableToRun() {
		return this.getStatusCode() == JobStatusCode.PENDING;
	}

	/**
	 * Checks all dependencies to determine if they are all successful.
	 * 
	 * @return True if all dependencies are successful.
	 */
	private Boolean dependenciesSatisfied() {
		return this.dependencies == null || this.dependencies.parallelStream()
				.allMatch(dependency -> dependency.getJob().getStatusCode().equals(dependency.getJobStatusCode()));
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
