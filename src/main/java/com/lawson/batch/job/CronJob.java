package com.lawson.batch.job;

import java.text.ParseException;
import java.util.Date;

import com.lawson.batch.exception.JobException;
import com.lawson.batch.util.CronExpression;

public abstract class CronJob extends Job {

	CronExpression cronExpression;

	public CronJob(final String name, final String cronExpression) {
		super(name);

		try {
			this.cronExpression = new CronExpression(cronExpression);
		} catch (ParseException e) {
			throw new JobException("Error creating cron expression", e);
		}
	}

	protected Boolean isSatisfiedBy(final Date tick) {
		if (this.cronExpression == null) {
			throw new JobException("Cron expression not specified");
		}

		return this.cronExpression.isSatisfiedBy(tick);
	}
}
