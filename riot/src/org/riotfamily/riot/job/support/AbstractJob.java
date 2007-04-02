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

import org.riotfamily.riot.dao.RiotDao;
import org.riotfamily.riot.job.Job;
import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.JobCreationException;
import org.riotfamily.riot.job.JobDescription;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

public abstract class AbstractJob implements Job, BeanNameAware {

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

	public JobDescription setup(String objectId) {
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
	
	public final void execute(final JobContext context) {
		if (transactionManager != null) {
			new TransactionTemplate(transactionManager).execute(new TransactionCallback() {
				public Object doInTransaction(TransactionStatus status) {
					execute(context, loadObject(context.getObjectId()));
					return null;
				}
			});
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
	
	protected abstract void execute(JobContext context, Object entity);
	
	public void tearDown(String objectId) {
	}

}
