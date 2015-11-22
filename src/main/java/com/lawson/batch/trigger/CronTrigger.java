package com.lawson.batch.trigger;

import java.text.ParseException;
import java.util.Date;

import com.lawson.batch.exception.JobException;
import com.lawson.batch.util.CronExpression;

public class CronTrigger implements Trigger {

	final CronExpression cronExpression;
	final Boolean isRepeatable;

	public CronTrigger(final String cronExpression, final Boolean isRepeatable) {
		try {
			this.cronExpression = new CronExpression(cronExpression);
		} catch (ParseException e) {
			throw new JobException("Error creating cron expression", e);
		}
		
		this.isRepeatable = isRepeatable;
	}
	
	@Override
	public Boolean isSatisfiedBy(Date tick) {
		return this.cronExpression.isSatisfiedBy(tick);
	}

	@Override
	public Boolean isRepeatable() {
		return this.isRepeatable;
	}

}
