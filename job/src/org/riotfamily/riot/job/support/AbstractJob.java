package org.riotfamily.riot.job.support;

import org.riotfamily.core.dao.RiotDao;
import org.riotfamily.riot.job.Job;
import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.JobCreationException;
import org.riotfamily.riot.job.JobDescription;
import org.riotfamily.riot.job.JobInterruptedException;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public abstract class AbstractJob implements Job, BeanNameAware {

	private static final DefaultTransactionDefinition TRANSACTION_DEFINITION =
		new DefaultTransactionDefinition(
		TransactionDefinition.PROPAGATION_REQUIRED);
	
	private String beanName;
	
	private RiotDao dao;
	
	private PlatformTransactionManager transactionManager;
	
	public void setDao(RiotDao dao) {
		this.dao = dao;
	}
	
	protected RiotDao getDao() {
		return this.dao;
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

	public boolean isConcurrent() {
		return false;
	}
	
	public boolean isRepeatable() {
		return true;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public JobDescription setup(final String objectId) {
		JobDescription jd = null;
		if (transactionManager != null) {
			TransactionStatus status = transactionManager.getTransaction(TRANSACTION_DEFINITION);
			try {
				 jd = setupInternal(objectId);
			}
			catch (JobCreationException e) {
				transactionManager.rollback(status);
				throw e;
			}
			transactionManager.commit(status);			
		}
		else {
			jd = setupInternal(objectId);
		}
		return jd;
	}
	
	protected final JobDescription setupInternal(String objectId) {
		JobDescription jd = new JobDescription();
		jd.setName(beanName);
		try {
			initDescription(jd, loadObject(objectId));
		}
		catch (Exception e) {
			throw new JobCreationException("Job creation failed.", e);
		}
		return jd;
	}
	
	protected void initDescription(JobDescription jd, Object entity) throws Exception {
	}
	
	public final void execute(final JobContext context) throws Exception {
		if (transactionManager != null) {
			
			TransactionStatus status = transactionManager.getTransaction(TRANSACTION_DEFINITION);
			try {
				execute(context, loadObject(context.getObjectId()));
			}
			catch (JobInterruptedException e) {				
			}
			catch (Exception e) {
				transactionManager.rollback(status);
				throw e;
			}
			transactionManager.commit(status);			
		}
		else {
			execute(context, loadObject(context.getObjectId()));
		}
	}
	
	protected Object loadObject(String objectId) {
		if (objectId != null) {
			return dao.load(objectId);
		}
		return null;
	}
	
	protected abstract void execute(JobContext context, Object entity)
			throws Exception;
	
	public void tearDown(String objectId) {
	}

}
