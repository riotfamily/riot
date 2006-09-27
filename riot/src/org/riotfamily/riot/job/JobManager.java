package org.riotfamily.riot.job;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.riot.job.persistence.JobDao;
import org.riotfamily.riot.job.persistence.JobDetail;
import org.riotfamily.riot.job.persistence.JobLogEntry;
import org.riotfamily.riot.job.support.ExecutionTimeUpdater;
import org.riotfamily.riot.job.support.JobTask;
import org.riotfamily.riot.job.support.TaskList;
import org.riotfamily.riot.job.ui.JobUIUpdater;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

public class JobManager implements ApplicationContextAware, DisposableBean {

	private static final String THREAD_NAME_PREFIX = "JobThread";
	
	private static Log log = LogFactory.getLog(JobManager.class);
	
	private Map jobs;
	
	JobDao dao;
	
	JobUIUpdater uiUpdater;
	
	private SimpleAsyncTaskExecutor taskExecutor = new SimpleAsyncTaskExecutor(
			THREAD_NAME_PREFIX);
	
	private TaskList taskList = new TaskList();
	
	private ExecutionTimeUpdater executionTimeUpdater = new ExecutionTimeUpdater(taskList);
	
	public JobManager(JobDao dao, JobUIUpdater uiUpdater) {
		this.dao = dao;
		this.uiUpdater = uiUpdater;
		taskExecutor.setThreadPriority(Thread.MIN_PRIORITY);
		taskExecutor.execute(executionTimeUpdater);
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
		List pendingJobs = dao.getPendingJobDetails();
		Iterator it = pendingJobs.iterator();
		while (it.hasNext()) {
			JobDetail jd = (JobDetail) it.next();
			if (taskList.getJobTask(jd) == null) {
				log.info("Job " + jd + " is not running - marking as INTERRUPTED.");
				jd.setState(JobDetail.INTERRUPTED);
				dao.updateJobDetail(jd);
			}
		}
	}
	
	protected Job getJob(String type) {
		return (Job) jobs.get(type);
	}
	
	public JobDetail getOrCreateJob(String type, String objectId) {
		JobDetail detail = dao.getPendingJobDetail(type, objectId);
		if (detail == null) {
			detail = setupJob(type, objectId);
			log.debug("No pending job found of type " + type 
					+ " for objectId '" + objectId + "' - a new job has been"
					+ " set up with id " + detail.getId());
		}
		else {
			log.debug("Found pending job: " + detail.getId());
		}
		return detail;
	}
	
	private JobDetail setupJob(String type, String objectId) {
		Job job = getJob(type);
		JobDescription desc = job.setup(objectId);
		int averageStepTime = 0;
		if (desc.getSteps() > 0) {
			averageStepTime = dao.getAverageStepTime(type);
		}
		JobDetail detail = new JobDetail(type, objectId, desc, averageStepTime);
		dao.saveJobDetail(detail);
		return detail;
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
		executionTimeUpdater.stop();
		taskList.interruptAll();
		log.debug("JobManager has been shut down.");
	}
}
