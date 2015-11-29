package com.lawson.batch.job.snapshot.visitor;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lawson.batch.job.Job;
import com.lawson.batch.job.JobStream;
import com.lawson.batch.job.snapshot.JobSnapshotVisitor;

public class JSONSnapshotVisitor implements JobSnapshotVisitor {

	@Override
	public JSONObject snapshot(Job job) {
		final JSONObject object = new JSONObject();

		object.put("id", job.getId());
		object.put("name", job.getName());
		object.put("statusCode", job.getStatusCode());

		return object;
	}

	@Override
	public JSONObject snapshot(JobStream jobStream) {
		final JSONObject object = new JSONObject();

		object.put("id", jobStream.getId());
		object.put("name", jobStream.getName());
		object.put("statusCode", jobStream.getStatusCode());

		jobStream.getJobs().forEach(job -> {
			final JSONArray jobArray = new JSONArray();
			jobArray.put(job.snapshot(this));

			object.put("jobs", jobArray);
		});

		return object;
	}

}
