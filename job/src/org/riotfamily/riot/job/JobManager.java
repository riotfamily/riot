/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.riot.job;

import java.util.Date;
import java.util.Map;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.riot.job.dao.JobDao;
import org.riotfamily.riot.job.model.JobDetail;
import org.riotfamily.riot.job.model.JobLogEntry;
import org.riotfamily.riot.job.support.JobTask;
import org.riotfamily.riot.job.support.TaskList;
import org.riotfamily.riot.job.ui.JobUIUpdater;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

public class JobManager implements ApplicationContextAware, DisposableBean {

	private static final String THREAD_NAME_PREFIX = "JobThread";
	
	private RiotLog log = RiotLog.get(JobManager.class);
	
	private Map<String, Job> jobs;
	
	JobDao dao;
	
	JobUIUpdater uiUpdater;
	
	private SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor(
			THREAD_NAME_PREFIX);
	
	private TaskList taskList = new TaskList();
	
	public JobManager(JobDao dao, JobUIUpdater uiUpdater) {
		this.dao = dao;
		this.uiUpdater = uiUpdater;
		taskExecutor.setThreadPriority(Thread.MIN_PRIORITY);
	}

	public void setApplicationContext(ApplicationContext context) {
		jobs = context.getBeansOfType(Job.class);
	}
	
	/**
	 * Checks whether there are any pending jobs which are not being executed.
	 * This implementation assumes that all jobs are run within the same
	 * virtual machine. To work in a clustered environment, this method must
	 * be overridden to check whether a job is run by another node before it
	 * is marked as aborted.
	 */
	protected void checkForAbortedJobs() {
		log.info("Checking for interrupted jobs ...");
		for (JobDetail jd : dao.getPendingJobDetails()) { 
			if (taskList.getJobTask(jd) == null) {
				log.info("Job " + jd + " is not running - marking as INTERRUPTED.");
				jd.setState(JobDetail.INTERRUPTED);
				dao.updateJobDetail(jd);
			}
		}
	}
	
	protected Job getJob(String type) {
		return jobs.get(type);
	}
	
	public JobDetail getOrCreateJob(String type, String objectId, boolean async) 
			throws JobCreationException {
		
		JobDetail detail = dao.getPendingJobDetail(type, objectId);
		if (detail == null) {
			detail = dao.getLastCompletedJobDetail(type, objectId);
			if (detail == null || getJob(type).isRepeatable()) {
				detail = setupJob(type, objectId, async);
				log.debug("No pending job found of type " + type 
						+ " for objectId '" + objectId + "' - a new job has "
						+ " been set up with id " + detail.getId());
			}
		}
		else {
			log.debug("Found pending job: " + detail.getId());
		}
		return detail;
	}
	
	private JobDetail setupJob(String type, String objectId, boolean async) 
			throws JobCreationException {
		
		int averageStepTime = dao.getAverageStepTime(type);
		JobDetail detail = new JobDetail(type, objectId, averageStepTime);
		dao.saveJobDetail(detail);
		if (async) {
			taskExecutor.execute(new JobSetupTask(detail));
		}
		else {
			doSetupJob(detail);
		}
		return detail;
	}
	
	private void doSetupJob(JobDetail detail) {
		Job job = getJob(detail.getType());
		detail.init(job.setup(detail.getObjectId()));
		dao.updateJobDetail(detail);
		uiUpdater.updateJob(detail);
	}
		
	public void executeJob(JobDetail detail) {
		log.info("Executing job " + detail);
		Job job = getJob(detail.getType());
		JobTask task = new JobTask(job, detail, dao, uiUpdater, taskList);
		taskExecutor.execute(task);
	}
	
	public void interruptJob(JobDetail detail) {
		log.info("Interrupting " + detail);
		taskList.interrupt(detail);
		JobLogEntry logEntry = new JobLogEntry(detail, "Job interrupted");
		dao.log(logEntry);
		uiUpdater.log(logEntry);
	}
	
	public void cancelJob(JobDetail detail) {
		log.info("Canceling " + detail);
		taskList.interrupt(detail);
		Job job = getJob(detail.getType());
		job.tearDown(detail.getObjectId());
		detail.setState(JobDetail.CANCELED);
		detail.setEndDate(new Date());
		dao.updateJobDetail(detail);
		uiUpdater.updateJob(detail);
		JobLogEntry logEntry = new JobLogEntry(detail, "Job canceled");
		dao.log(logEntry);
		uiUpdater.log(logEntry);
	}
	
	public void destroy() throws Exception {
		taskList.interruptAll();
		log.debug("JobManager has been shut down.");
	}
	
	private class JobSetupTask implements Runnable {

		private JobDetail detail;
		
		public JobSetupTask(JobDetail detail) {
			this.detail = detail;
		}

		public void run() {
			doSetupJob(detail);
		}
	}
}
