# SimpleBatch

Lightweight and simple batch processing framework.

# Usage

The basic usage revolves around a `JobStream` and a `Job`.  A `JobStream` is technically a `Job` itself, but is meant to simplify grouping of jobs and wiring the dependencies between jobs.

Both a `JobStream` and `Job` use the [composite pattern](https://en.wikipedia.org/wiki/Composite_pattern) and can therefore be nested as needed.

# Example

```
final JobStream rootJobStream = new JobStream("Root");
final JobStreamRunner runnner = new JobStreamRunner(rootJobStream, Level.FINER);
		
final JobStream rootChild = new SerialJobStream("RootChildJobStream");
		
rootChild.addJob(new Job("Job1") {
	public void process(JobStreamContext context, Date tick, Object data) {
		setStatusCode(JobStatusCode.SUCCESS);
	}
});

rootChild.addJob(new Job("Job2") {
	public void process(JobStreamContext context, Date tick, Object data) {
		setStatusCode(JobStatusCode.SUCCESS);
	}
});

rootJobStream.addJob(rootChild);

runnner.start();
```
