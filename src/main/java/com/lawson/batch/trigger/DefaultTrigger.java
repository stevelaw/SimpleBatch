package com.lawson.batch.trigger;

import java.util.Date;

public class DefaultTrigger implements Trigger {

	@Override
	public Boolean isSatisfiedBy(Date tick) {
		return true;
	}

	@Override
	public Boolean isRepeatable() {
		return false;
	}

}
