package com.lawson.batch.util;

public class Stopwatch {
	private final long start;

	/**
	 * Initializes a new stopwatch.
	 */
	public Stopwatch() {
		start = System.nanoTime();
	}

	/**
	 * Returns the elapsed CPU time (in seconds) since the stopwatch was
	 * created.
	 *
	 * @return elapsed CPU time (in seconds) since the stopwatch was created
	 */
	public double elapsedTime() {
		long now = System.nanoTime();
		return (now - start) / 1000;
	}
}
