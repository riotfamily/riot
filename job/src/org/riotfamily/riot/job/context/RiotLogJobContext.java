package org.riotfamily.riot.job.context;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.JobInterruptedException;

public class RiotLogJobContext implements JobContext {
	
	private RiotLog log;
	
	private int stepsTotal;
	
	private int stepsCompleted;
	
	public RiotLogJobContext() {
		log = RiotLog.get(RiotLogJobContext.class);
	}

	public RiotLogJobContext(RiotLog log) {
		this.log = log;
	}

	public String getObjectId() {
		return null;
	}

	public void logError(String message) {
		log.warn(message);
	}

	public void logInfo(String message) {
		if (stepsTotal > 0) {
			message = "[" + (stepsCompleted * 100 / stepsTotal) + "%] " + message;
		}
		log.info(message);
	}

	public void stepCompleted() throws JobInterruptedException {
		stepsCompleted++;
	}
	
	public void updateStepsTotal(int stepsTotal, boolean resetStepsCompleted) {
		this.stepsTotal = stepsTotal;
		if (resetStepsCompleted) {
			stepsCompleted = 0;
		}
	}
	
	public void updateDescription(String description) {
	}

}
