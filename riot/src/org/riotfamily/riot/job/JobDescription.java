package org.riotfamily.riot.job;

/**
 * Description created by the {@link org.riotfamily.riot.job.Job#setup(String) 
 * setup()}-method.
 */
public class JobDescription {

	private String name;
	
	private String description;

	private int steps;

	
	public JobDescription() {
	}

	public JobDescription(String name, int steps) {
		this.name = name;
		this.steps = steps;
	}
	
	public JobDescription(String name, String description, int steps) {
		this.name = name;
		this.description = description;
		this.steps = steps;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns a String that describes what the job is/will be doing.
	 */
	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Returns the number of steps needed to complete the job. This information
	 * is used to provide progress information.
	 */
	public int getSteps() {
		return this.steps;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
	
}
