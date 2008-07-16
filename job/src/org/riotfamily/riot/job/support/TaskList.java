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

import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.util.Generics;
import org.riotfamily.riot.job.model.JobDetail;

public class TaskList {

	private Log log = LogFactory.getLog(TaskList.class);
	
	private HashSet<JobTask> activeTasks = Generics.newHashSet();
	
	/**
	 * Returns the JobTask for the given JobDetail or <code>null</code> if
	 * no such task exists.
	 */
	public synchronized JobTask getJobTask(JobDetail detail) {
		for (JobTask task : activeTasks) {
			if (task.getDetail().getId().equals(detail.getId())) {
				return task;
			}
		}
		return null;
	}
	
	/**
	 * Adds the given task to the list of active tasks and notifies waiting
	 * threads.
	 */
	public synchronized void addTask(JobTask task) {
		activeTasks.add(task);
		notifyAll();
	}
	
	/**
	 * Removes the given task from the list of active tasks.
	 */
	public synchronized void removeTask(JobTask task) {
		activeTasks.remove(task);
	}
		
	public void interrupt(JobDetail detail) {
		JobTask task = getJobTask(detail);
		if (task != null) {
			task.interrupt();
			removeTask(task);
		}
	}
	
	public void interruptAll() {
		//TODO Synchronization ...
		for (JobTask task : activeTasks) {
			log.info("Interrupting task " + task.getDetail().getId());
			task.interrupt();
		}
		activeTasks.clear();
	}
	
	/**
	 * Iterates over the active tasks and invokes 
	 * {@link JobTask#updateExecutionTime() task.updateExecutionTime()}.
	 */
	public synchronized void updateExecutionTimes() {
		for (JobTask task : activeTasks) {
			task.updateExecutionTime();
		}
	}
	
	/**
	 * Waits until at least one active task is present.
	 */
	public synchronized void waitForTasks() {
		if (activeTasks.isEmpty()) {
			try {
				wait();
			}
			catch (InterruptedException e) {
			}
		}
	}
	
}
