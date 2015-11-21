package com.lawson.batch.exception;

public class JobException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public JobException(final String message) {
		super(message);
	}
}
