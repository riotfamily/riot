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
package org.riotfamily.riot.job.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.riot.hibernate.support.HibernateHelper;
import org.riotfamily.riot.job.model.JobDetail;
import org.riotfamily.riot.job.model.JobLogEntry;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateJobDao implements JobDao {
	
	private HibernateHelper hibernate;
	
	public HibernateJobDao(SessionFactory sessionFactory) {		
		this.hibernate = new HibernateHelper(sessionFactory);
	}

	public Collection<JobDetail> getJobDetails() {
		Query query = hibernate.createQuery("from JobDetail job " +
				"order by job.endDate desc");
		return hibernate.list(query);
	}
	
	public Collection<JobDetail> getPendingJobDetails() {
		Query query = hibernate.createQuery("from JobDetail job where " +
				"job.state != " + JobDetail.CANCELED + " and " +
				"job.state != " + JobDetail.COMPLETED + 
				"order by job.startDate desc");
		
		return hibernate.list(query);
	}
	
	public JobDetail getPendingJobDetail(String type, String objectId) {
		Query query = hibernate.createQuery("from JobDetail job where " +
				"job.state != " + JobDetail.CANCELED + " and " +
				"job.state != " + JobDetail.COMPLETED + " and " +
				"job.type = :type and (:objectId is null or job.objectId = :objectId)" +
				"order by job.startDate desc");
		
		query.setParameter("type", type);
		query.setParameter("objectId", objectId);
		query.setMaxResults(1);
		
		List<JobDetail> jobs = hibernate.list(query);
		if (jobs.isEmpty()) {
			return null;
		}
		return jobs.get(0);
	}
	
	public JobDetail getLastCompletedJobDetail(String type, String objectId) {
		Query query = hibernate.createQuery("from JobDetail job where " +
				"job.state = " + JobDetail.COMPLETED  + " and " +
				"job.type = :type and (:objectId is null or job.objectId = :objectId)" +
				"order by job.startDate desc");
		
		query.setParameter("type", type);
		query.setParameter("objectId", objectId);
		query.setMaxResults(1);
		
		List<JobDetail> jobs = hibernate.list(query);
		if (jobs.isEmpty()) {
			return null;
		}
		return jobs.get(0);
	}
	
	public int getAverageStepTime(String type) {
		Query query = hibernate.createQuery("select avg(averageStepTime) from " +
				"JobDetail where stepsCompleted > 0 and " + "type = :type");
			
		query.setParameter("type", type);
		Number time = hibernate.uniqueResult(query);
		if (time == null) {
			return 0;
		}
		return time.intValue();
	}
	
	public JobDetail getJobDetail(Long id) {
		return hibernate.get(JobDetail.class, id);
	}
	
	public void saveJobDetail(JobDetail job) {
		hibernate.save(job);
	}

	public void updateJobDetail(JobDetail job) {
		hibernate.update(job);
	}

	public Collection<JobLogEntry> getLogEntries(Long jobId) {
		Query query = hibernate.createQuery("from JobLogEntry e where " +
				"e.job.id = :jobId order by e.date desc");
		
		query.setParameter("jobId", jobId);
		return hibernate.list(query);
	}

	public void log(JobLogEntry entry) {
		hibernate.save(entry);
	}

}
