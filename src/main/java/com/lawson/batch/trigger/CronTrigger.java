package com.lawson.batch.trigger;

import java.text.ParseException;
import java.util.Date;

import com.lawson.batch.exception.JobException;
import com.lawson.batch.util.CronExpression;

public class CronTrigger implements Trigger {

	CronExpression cronExpression;

	public CronTrigger(final String cronExpression) {
		try {
			this.cronExpression = new CronExpression(cronExpression);
		} catch (ParseException e) {
			throw new JobException("Error creating cron expression", e);
		}
	}
	
	@Override
	public Boolean isSatisfiedBy(Date tick) {
		// TODO Auto-generated method stub
		return null;
	}

}
