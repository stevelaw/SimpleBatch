package com.lawson.batch;

import java.time.ZoneId;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;

public class JobStreamRunnerConfig {
	private static final String DEFAULT_ZONE_ID_STRING = "America/New_York";

	private Level logLevel;
	private List<Handler> logHandlers;
	private ZoneId timezoneZoneId;

	public static class Builder {
		private Level logLevel = Level.ALL;
		private List<Handler> logHandlers;
		private ZoneId timezoneZoneId = ZoneId.of(DEFAULT_ZONE_ID_STRING);

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

		public JobStreamRunnerConfig build() {
			return new JobStreamRunnerConfig(this);
		}
	}

	private JobStreamRunnerConfig(final Builder builder) {
		this.logLevel = builder.logLevel;
		this.logHandlers = builder.logHandlers;
		this.timezoneZoneId = builder.timezoneZoneId;
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
}
