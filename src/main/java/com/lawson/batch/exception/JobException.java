package com.lawson.batch.exception;

import java.text.ParseException;

public class JobException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public JobException(final String message) {
		super(message);
	}

	public JobException(String message, ParseException e) {
		super(message, e);
	}
}
