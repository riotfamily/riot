package org.riotfamily.riot.job.support;

import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.job.Job;
import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.JobDescription;
import org.springframework.beans.factory.BeanNameAware;

public abstract class AbstractJob implements Job, BeanNameAware {

	private boolean concurrent;
	
	private String beanName;
	
	private RiotDao dao;
	
	public void setDao(RiotDao dao) {
		this.dao = dao;
	}
	
	protected RiotDao getDao() {
		return this.dao;
	}

	public void setConcurrent(boolean concurrent) {
		this.concurrent = concurrent;
	}

	public boolean isConcurrent() {
		return concurrent;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public JobDescription setup(String objectId) {
		JobDescription jd = new JobDescription();
		jd.setName(beanName);
		return jd;
	}
	
	public final void execute(JobContext context) {
		execute(context, dao.load(context.getObjectId()));
	}
	
	protected abstract void execute(JobContext context, Object entity);
	
	public void tearDown(String objectId) {
		// TODO
	}

}
