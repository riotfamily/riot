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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.job;

import org.riotfamily.riot.job.support.JobTask;


public class JobContext {

	private JobTask task;
	
	public JobContext(JobTask task) {
		this.task = task;
	}

	/**
	 * Returns the objectId.
	 */
	public String getObjectId() {
		return task.getDetail().getObjectId();
	}

	/**
	 * Notifies the DAO that a step has been completed.
	 * @throws JobInterruptedException if the job has been interrupted 
	 */
	public void stepCompleted() throws JobInterruptedException {
		task.stepCompleted();	
	}
			
	/**
	 * Logs an info message.
	 */
	public void logInfo(String message) {
		task.logInfo(message);
	}
	
	/**
	 * Logs an error message.
	 */
	public void logError(String message) {
		task.logError(message);
	}
		
}
