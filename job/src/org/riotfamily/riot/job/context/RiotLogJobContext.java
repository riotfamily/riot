/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Jan-Frederic Linde [jfl at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.job.context;

import org.riotfamily.common.log.RiotLog;
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
