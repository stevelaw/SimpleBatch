package com.lawson.batch.clock;

import java.util.Date;

public interface JobClockHandler {
	public void onTick(final Date tick);
}
