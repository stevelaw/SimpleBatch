package com.lawson.batch;

import com.lawson.batch.util.JobStatusCode;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JobStatus {
	private JobStatusCode statusCode;
	private Object data;
}
