package com.lawson.batch;

import java.time.ZoneId;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;

public class JobStreamRunnerConfig {
	private static final Level DEFAULT_LOG_LEVEL = Level.ALL;
	private static final String DEFAULT_ZONE_ID_STRING = "America/New_York";
	private static final int DEFAULT_NUMBER_DISPATCHER_THREADS = 10;
	private static final long DEFAULT_TIMER_TICK_INTERVAL_MS = 1000;

	private Level logLevel;
	private List<Handler> logHandlers;
	private ZoneId timezoneZoneId;
	private Integer numberOfDispatcherThreads;
	private long timerTickIntervalMS;

	public static class Builder {
		private Level logLevel = DEFAULT_LOG_LEVEL;
		private List<Handler> logHandlers;
		private ZoneId timezoneZoneId = ZoneId.of(DEFAULT_ZONE_ID_STRING);
		private int numberOfDispatcherThreads = DEFAULT_NUMBER_DISPATCHER_THREADS;
		private long timerTickIntervalMS = DEFAULT_TIMER_TICK_INTERVAL_MS;

		public Builder logLevel(final Level logLevel) {
			if (logLevel == null) {
				throw new IllegalArgumentException("Log level cannot be null");
			}

			this.logLevel = logLevel;

			return this;
		}

		public Builder logHandlers(final List<Handler> logHandlers) {
			this.logHandlers = logHandlers;

			return this;
		}

		public Builder timezoneZoneId(final ZoneId timezoneZoneId) {
			if (timezoneZoneId == null) {
				throw new IllegalArgumentException("Timezone ID cannot be null");
			}

			this.timezoneZoneId = timezoneZoneId;

			return this;
		}

		public Builder numberOfDispatcherThreads(final int numberOfDispatcherThreads) {
			if (numberOfDispatcherThreads <= 0) {
				throw new IllegalArgumentException("Number of dispatcher threads must be greater than 0");
			}

			this.numberOfDispatcherThreads = numberOfDispatcherThreads;

			return this;
		}
		
		public Builder timerTickIntervalMS(final long timerTickIntervalMS) {
			if (timerTickIntervalMS <= 0) {
				throw new IllegalArgumentException("Timer tick interval must be greater than 0");
			}

			this.timerTickIntervalMS = timerTickIntervalMS;

			return this;
		}

		public JobStreamRunnerConfig build() {
			return new JobStreamRunnerConfig(this);
		}
	}

	private JobStreamRunnerConfig(final Builder builder) {
		this.logLevel = builder.logLevel;
		this.logHandlers = builder.logHandlers;
		this.timezoneZoneId = builder.timezoneZoneId;
		this.numberOfDispatcherThreads = builder.numberOfDispatcherThreads;
		this.timerTickIntervalMS = builder.timerTickIntervalMS;
	}

	public Level getLogLevel() {
		return logLevel;
	}

	public List<Handler> getLogHandlers() {
		return logHandlers;
	}

	public ZoneId getTimezoneZoneId() {
		return timezoneZoneId;
	}

	public Integer getNumberDispatcherThreads() {
		return numberOfDispatcherThreads;
	}

	public long getTimerTickIntervalMS() {
		return timerTickIntervalMS;
	}
}
