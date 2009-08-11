package org.riotfamily.riot.job.dao;

import java.util.Calendar;
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
	
	@SuppressWarnings("unchecked")
	public void deleteObsoleteJobDetails() {
		Query query = hibernate.createQuery("from JobDetail job where " +
				"job.startDate < :date");
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -7);
		query.setDate("date", cal.getTime());
		
		List<JobDetail> details = query.list();
		for (JobDetail detail : details) {
			hibernate.delete(detail);
		}
	}
	
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
		Query query = hibernate.createQuery(hql.toString());
		
		hibernate.setParameter(query, "type", type);
		hibernate.setParameter(query, "objectId", objectId);
		query.setMaxResults(1);
		
		List<JobDetail> jobs = hibernate.list(query);
		if (jobs.isEmpty()) {
			return null;
		}
		return jobs.get(0);
	}
	
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
		Query query = hibernate.createQuery(hql.toString());
		
		hibernate.setParameter(query, "type", type);
		hibernate.setParameter(query, "objectId", objectId);
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
