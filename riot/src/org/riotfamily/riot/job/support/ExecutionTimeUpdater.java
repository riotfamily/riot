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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.job.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.riot.job.JobContext;

/**
 * Task that updates the executionTime of active jobs. This is also done when 
 * {@link JobContext#stepCompleted() JobContext.stepCompleted()} is invoked,
 * but in case a job takes a very long time to complete a step or it does not
 * inform the JobContext about its progress, this fallback ensures that the
 * executionTime is properly updated. 
 */
public class ExecutionTimeUpdater implements Runnable {

	private Log log = LogFactory.getLog(ExecutionTimeUpdater.class);
	
	private TaskList taskList;
	
	private long updateInterval = 15000;
	
	private volatile Thread thread;
	
	public ExecutionTimeUpdater(TaskList taskList) {
		this.taskList = taskList;
	}

	public void setUpdateInterval(long updateInterval) {
		this.updateInterval = updateInterval;
	}

	public void stop() {
		Thread thread = this.thread;
		this.thread = null;
		thread.interrupt();
	}
	
	public void run() {
		thread = Thread.currentThread();
		while (thread != null) {
			taskList.updateExecutionTimes();
			try {
				Thread.sleep(updateInterval);
				taskList.waitForTasks();
			}
			catch (InterruptedException e) {
			}
		}
		log.info("ExecutionTimeUpdater has been stopped.");
	}

}
