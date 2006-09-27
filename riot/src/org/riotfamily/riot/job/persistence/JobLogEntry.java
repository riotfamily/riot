package org.riotfamily.riot.job.persistence;

import java.util.Date;


public class JobLogEntry {

	public static final int INFO = 1;
	
	public static final int ERROR = 2;
	
	private Long id;
	
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
		this.message = message;
		this.date = new Date();
	}

	public Long getId() {
		return this.id;
	}

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
