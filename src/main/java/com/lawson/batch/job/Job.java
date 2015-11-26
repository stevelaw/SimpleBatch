package com.lawson.batch.job;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.logging.Logger;

import com.lawson.batch.clock.JobClock;
import com.lawson.batch.clock.JobClockHandler;
import com.lawson.batch.jobstream.JobStream;
import com.lawson.batch.trigger.DefaultTrigger;
import com.lawson.batch.trigger.Trigger;
import com.lawson.batch.util.JobStatusCode;
import com.lawson.batch.util.Stopwatch;

public abstract class Job implements JobClockHandler {
	private final static Logger LOGGER = Logger.getLogger(JobStream.class.getName());

	private UUID id;
	private String name;
	private volatile JobStatusCode statusCode = JobStatusCode.PENDING;
	private List<Job> dependencies;
	private Object data;
	private Trigger trigger;
	private JobClock jobClock;

	Stopwatch stopwatch;

	public Job(final String name) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.dependencies = new ArrayList<>();
		this.trigger = new DefaultTrigger();
	}

	public Job(final String name, final Trigger trigger) {
		this.id = UUID.randomUUID();
		this.name = name;
		this.dependencies = new ArrayList<>();

		if (trigger == null) {
			this.trigger = new DefaultTrigger();
		} else {
			this.trigger = trigger;
		}
	}

	public void setDependencies(final List<Job> dependencies) {
		this.dependencies = dependencies;
	}

	public void setDependency(final Job dependency) {
		this.dependencies = new ArrayList<>();
		this.dependencies.add(dependency);
	}

	public List<Job> getDependencies() {
		return this.dependencies;
	}

	public Optional<Job> getDependencyByName(final String jobName) {
		return this.dependencies.stream().filter(job -> job.getName().equals(jobName)).findFirst();
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setJobClock(JobClock jobClock) {
		this.jobClock = jobClock;
	}

	public JobClock getJobClock() {
		return jobClock;
	}

	public Object getData() {
		return data;
	}

	public void preProcess(final Date tick, final Object data) {
	}

	public abstract void process(final Date tick, final Object data);

	public void postProcess(final Date tick, final Object data) {
	}

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

	public void setStatusAndData(final JobStatusCode statusCode, final Object data) {
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
	 * Returns true if available to run (pending).
	 * 
	 * @return True if available to run (pending).
	 */
	protected boolean isAvailableToRun() {
		return this.getStatusCode() == JobStatusCode.PENDING;
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

	private Map<String, Object> getJobDependencyDataByJobName() {
		// Collect Map of data from dependencies to pass onto next job
		final Map<String, Object> jobDataByName = new HashMap<>(this.dependencies.size());

		this.dependencies.forEach(job -> jobDataByName.put(job.getName(), job.getData()));

		return jobDataByName;
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
