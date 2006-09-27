package org.riotfamily.riot.job.persistence;

import java.util.Date;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.job.JobDescription;

/**
 * Detailed information about a job.  
 */
public class JobDetail {

	public static final int STARTED = 1;
	
	public static final int INTERRUPTED = 2;
	
	public static final int CANCELED = 3;
	
	public static final int COMPLETED = 4;
	
	private Long id;
	
	private String type;
	
	private String name;
	
	private String description;
	
	private int state;
	
	private Date startDate;
	
	private Date endDate;
	
	private int stepsTotal;
	
	private int stepsCompleted;
	
	private long lastStepTime;
	
	private long executionTime; 
	
	private int averageStepTime;
	
	private String objectId;
	
	
	public JobDetail() {
	}

	public JobDetail(String type, String objectId, JobDescription desc, 
			int averageStepTime) {
		
		this.type = type;
		this.objectId = objectId;
		this.name = desc.getName();
		this.description = desc.getDescription();
		this.stepsTotal = desc.getSteps();
		this.averageStepTime = averageStepTime;
	}
	
	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	/**
	 * Returns the job type. The returned String can be used to lookup a
	 * Job implementation using the JobManager.
	 */
	public String getType() {
		return this.type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getState() {
		return this.state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getObjectId() {
		return this.objectId;
	}

	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}

	public Date getStartDate() {
		return this.startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	
	/**
	 * Returns the date when the job was completed or <code>null</code> if
	 * the job is still pending.
	 */
	public Date getEndDate() {
		return this.endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	
	public int getStepsTotal() {
		return this.stepsTotal;
	}

	public void setStepsTotal(int stepsTotal) {
		this.stepsTotal = stepsTotal;
	}
	
	public int getStepsCompleted() {
		return this.stepsCompleted;
	}

	public void setStepsCompleted(int stepsCompleted) {
		this.stepsCompleted = stepsCompleted;
	}
	
	public void stepCompleted() {
		stepsCompleted++;
		updateExecutionTime();
	}

	
	public long getExecutionTime() {
		return this.executionTime;
	}

	public void setExecutionTime(long executionTime) {
		this.executionTime = executionTime;
	}
	
	public int getAverageStepTime() {
		return this.averageStepTime;
	}

	public void setAverageStepTime(int averageStepTime) {
		this.averageStepTime = averageStepTime;
	}

	public void updateExecutionTime() {
		long now = System.currentTimeMillis();
		if (lastStepTime > 0) {
			long millis = now - lastStepTime;
			this.executionTime += millis;
		}
		lastStepTime = now;
		if (stepsTotal > 0 && stepsCompleted > 0) {
			averageStepTime = (int) (executionTime / stepsCompleted);
		}
	}

	public String getElapsedTime() {
		return FormatUtils.formatMillis(executionTime);
	}
	
	public String getEstimatedTime() {
		if (averageStepTime > 0 && stepsTotal > 0) {
			long eta = averageStepTime * stepsTotal - executionTime;
			return FormatUtils.formatMillis(eta);
		}
		return null;
	}
	
	public int getProgress() {
		if (stepsTotal > 0) {
			return stepsCompleted * 100 / stepsTotal;
		}
		return -1;
	}
	
	public String toString() {
		return "Job " + id + ": " + name;
	}
	
}
