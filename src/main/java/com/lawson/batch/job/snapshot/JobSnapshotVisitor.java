package com.lawson.batch.job.snapshot;

import com.lawson.batch.job.Job;
import com.lawson.batch.job.JobStream;

public interface JobSnapshotVisitor {
	public Object snapshot(final Job job);
	public Object snapshot(final JobStream jobStream);
}
