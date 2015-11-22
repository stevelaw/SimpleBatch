package com.lawson.batch.trigger;

import java.util.Date;

public interface Trigger {
	public Boolean isSatisfiedBy(final Date tick);
	
	public Boolean isRepeatable();
}
