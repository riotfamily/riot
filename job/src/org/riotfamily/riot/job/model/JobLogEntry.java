package org.riotfamily.riot.job.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Index;
import org.riotfamily.common.util.FormatUtils;


@Entity
@Table(name="riot_job_log")
public class JobLogEntry {

	public static final int INFO = 1;
	
	public static final int ERROR = 2;
	
	@SuppressWarnings("unused")
	@Id @GeneratedValue(strategy=GenerationType.AUTO)	
	private Long id;
	
	@ManyToOne
	@Index(name="riot_job_log_job_id")
	private JobDetail job;
	
	private Date date;
	
	private int priority;
	
	private String message;

	public JobLogEntry() {
	}
	
	public JobLogEntry(JobDetail job, String message) {
		this(job, INFO, message);
	}

	public JobLogEntry(JobDetail job, int priority, String message) {
		this.job = job;
		this.priority = priority;
		this.message = FormatUtils.truncate(message, 255);
		this.date = new Date();
	}
	
	@Transient
	public Long getJobId() {
		return job.getId();
	}
	
	public Date getDate() {
		return this.date;
	}

	public String getMessage() {
		return this.message;
	}
	
	public int getPriority() {
		return this.priority;
	}
	
}
