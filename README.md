# SimpleBatch

Lightweight and simple batch processing framework.

# Usage

The basic usage revolves around the `Job`, `JobStream` and `Trigger` objects.  A `Job` is used to define the work to be done.  A `JobStream` is an extension a `Job` itself, and is meant to simplify grouping of jobs and wiring the dependencies between jobs. Finally, a `Trigger` is used to determine when the `Job` or `JobStream` is available to run. For example, a `CronTrigger` can be used to cause a `Job` or `JobStream` to only run when the Cron expression condition is met.

Both a `JobStream` and `Job` use the [composite pattern](https://en.wikipedia.org/wiki/Composite_pattern) and can therefore be nested as needed.

# Example

```
final JobStream rootJobStream = new SerialJobStream("Root");
		
final JobStream rootChild = new SerialJobStream("RootChildJobStream");
		
rootChild.addJob(new Job("Job1") {
	public void process(JobStreamContext context, Date tick, Object data) {
		// Do something
		
		setStatusCode(JobStatusCode.SUCCESS);
	}
});

rootChild.addJob(new Job("Job2") {
	public void process(JobStreamContext context, Date tick, Object data) {
		// Do something
		
		setStatusCode(JobStatusCode.SUCCESS);
	}
});

rootJobStream.addJob(rootChild);

final JobStreamRunner runnner = new JobStreamRunner(rootJobStream, Level.FINER);
runnner.start();
```
