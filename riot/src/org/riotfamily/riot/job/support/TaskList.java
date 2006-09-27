package org.riotfamily.riot.job.support;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.riot.job.persistence.JobDetail;

public class TaskList {

	private static Log log = LogFactory.getLog(TaskList.class);
	
	private HashSet activeTasks = new HashSet();
	
	/**
	 * Returns the JobTask for the given JobDetail or <code>null</code> if
	 * no such task exists.
	 */
	public synchronized JobTask getJobTask(JobDetail detail) {
		Iterator it = activeTasks.iterator();
		while (it.hasNext()) {
			JobTask task = (JobTask) it.next();
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
		Iterator it = activeTasks.iterator();
		while (it.hasNext()) {
			JobTask task = (JobTask) it.next();
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
		Iterator it = activeTasks.iterator();
		while (it.hasNext()) {
			JobTask task = (JobTask) it.next();
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
