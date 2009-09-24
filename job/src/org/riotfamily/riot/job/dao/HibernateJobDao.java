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
package org.riotfamily.riot.job.dao;

import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.riotfamily.riot.job.model.JobDetail;
import org.riotfamily.riot.job.model.JobLogEntry;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class HibernateJobDao extends HibernateDaoSupport implements JobDao {
	
	public HibernateJobDao(SessionFactory sessionFactory) {		
		setSessionFactory(sessionFactory);
	}

	@SuppressWarnings("unchecked")
	public Collection<JobDetail> getJobDetails() {
		return getSession().createQuery("from JobDetail job " +
				"order by job.endDate desc").list();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<JobDetail> getPendingJobDetails() {
		return getSession().createQuery("from JobDetail job where " +
				"job.state != " + JobDetail.CANCELED + " and " +
				"job.state != " + JobDetail.COMPLETED + 
				"order by job.startDate desc").list();
	}
	
	@SuppressWarnings("unchecked")
	public void deleteObsoleteJobDetails() {
		Query query = getSession().createQuery("from JobDetail job where " +
				"job.startDate < :date");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		query.setDate("date", cal.getTime());
		
		List<JobDetail> details = query.list();
		for (JobDetail detail : details) {
			getSession().delete(detail);
		}
	}
	
	@SuppressWarnings("unchecked")
	public JobDetail getPendingJobDetail(String type, String objectId) {
		StringBuffer hql = new StringBuffer();
		hql.append("from JobDetail job where")
		   .append(" job.state != ")
		   .append(JobDetail.CANCELED)
		   .append(" and job.state != ")
		   .append(JobDetail.COMPLETED)
		   .append(" and job.type = :type");
		if (objectId != null) {
			hql.append(" and job.objectId = :objectId");
		}
		hql.append(" order by job.startDate desc");
		Query query = getSession().createQuery(hql.toString())
				.setParameter("type", type)
				.setParameter("objectId", objectId)
				.setMaxResults(1);
		
		List<JobDetail> jobs = query.list();
		if (jobs.isEmpty()) {
			return null;
		}
		return jobs.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public JobDetail getLastCompletedJobDetail(String type, String objectId) {
		StringBuffer hql = new StringBuffer();
		hql.append("from JobDetail job where")
		   .append(" job.state = ")
		   .append(JobDetail.COMPLETED)
		   .append(" and job.type = :type");
		if (objectId != null) {
			hql.append(" and job.objectId = :objectId");
		}
		hql.append(" order by job.startDate desc");
		Query query = getSession().createQuery(hql.toString())
				.setParameter("type", type)
				.setParameter("objectId", objectId)
				.setMaxResults(1);
		
		List<JobDetail> jobs = query.list();
		if (jobs.isEmpty()) {
			return null;
		}
		return jobs.get(0);
	}
	
	public int getAverageStepTime(String type) {
		Query query = getSession().createQuery("select avg(averageStepTime) from " +
				"JobDetail where stepsCompleted > 0 and " + "type = :type")
				.setParameter("type", type);
		
		Number time = (Number) query.uniqueResult();
		if (time == null) {
			return 0;
		}
		return time.intValue();
	}
	
	public JobDetail getJobDetail(Long id) {
		return (JobDetail) getSession().get(JobDetail.class, id);
	}
	
	public void saveJobDetail(JobDetail job) {
		getSession().save(job);
	}

	public void updateJobDetail(JobDetail job) {
		getSession().update(job);
	}

	@SuppressWarnings("unchecked")
	public Collection<JobLogEntry> getLogEntries(Long jobId) {
		Query query = getSession().createQuery("from JobLogEntry e where " +
				"e.job.id = :jobId order by e.date desc");
		
		query.setParameter("jobId", jobId);
		return query.list();
	}

	public void log(JobLogEntry entry) {
		getSession().save(entry);
	}

}
