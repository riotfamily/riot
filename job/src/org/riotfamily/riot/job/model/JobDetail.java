package org.riotfamily.riot.job.model;

import java.util.Date;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Type;
import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.riot.job.JobDescription;

/**
 * Detailed information about a job.  
 */
@Entity
@Table(name="riot_jobs")
public class JobDetail {

	public static final int NEW = -1;
	
	public static final int INITIALIZED = 0;
	
	public static final int STARTED = 1;
	
	public static final int INTERRUPTED = 2;
	
	public static final int CANCELED = 3;
	
	public static final int COMPLETED = 4;
	
	private Long id;
	
	private String type;
	
	private String name;
	
	private String description;
	
	private int state = NEW;
	
	private Date startDate;
	
	private Date endDate;
	
	private int stepsTotal;
	
	private int stepsCompleted;
	
	private long lastStepTime;
	
	private long executionTime; 
	
	private int averageStepTime;
	
	private String objectId;
	
	private Set<JobLogEntry> log;
	
	public JobDetail() {
	}

	public JobDetail(String type, String objectId, int averageStepTime) {
		this.type = type;
		this.objectId = objectId;
		this.averageStepTime = averageStepTime;
	}
	
	public void init(JobDescription desc) {
		this.name = desc.getName();
		this.description = desc.getDescription();
		this.stepsTotal = desc.getSteps();
		this.state = INITIALIZED;
	}
	
	@Id @GeneratedValue(strategy=GenerationType.AUTO)
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
	
	@Type(type="text")
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

	@Transient
	public String getElapsedTime() {
		return FormatUtils.formatMillis(executionTime);
	}
	
	@Transient
	public String getEstimatedTime() {
		if (averageStepTime > 0 && stepsTotal > 0) {
			long eta = averageStepTime * stepsTotal - executionTime;
			if (eta < 0) {
				eta = 0;
			}
			return FormatUtils.formatMillis(eta);
		}
		return null;
	}
	
	@Transient
	public int getProgress() {
		if (stepsTotal > 0) {
			return stepsCompleted * 100 / stepsTotal;
		}
		return -1;
	}
	
	public String toString() {
		return "Job " + id + ": " + name;
	}

	@OneToMany(mappedBy="job", cascade=CascadeType.ALL, fetch=FetchType.EAGER)
	@OrderBy("date")
	public Set<JobLogEntry> getLog() {
		return log;
	}

	public void setLog(Set<JobLogEntry> log) {
		this.log = log;
	}
	
}
