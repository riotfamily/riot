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
package org.riotfamily.riot.job.support;

import org.riotfamily.riot.job.Job;
import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.JobDescription;

public class TestJob implements Job {

	private String name = "Test Job";
	
	private String description;
	
	private int steps = 50;
	
	private long delay = 1000;
	
	private int errorAfter = 0;
	
	private boolean reportProgress = true;
	
	private int step = 1;
	
	public void setName(String name) {
		this.name = name;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setSteps(int steps) {
		this.steps = steps;
	}
	
	public void setDelay(long delay) {
		this.delay = delay;
	}

	public void setReportProgress(boolean reportProgress) {
		this.reportProgress = reportProgress;
	}

	public void setErrorAfter(int errorAfter) {
		this.errorAfter = errorAfter;
	}

	public JobDescription setup(String objectId) {
		return new JobDescription(name, description, reportProgress ? steps : 0);
	}
	
	public void execute(JobContext context) {
		while (step <= steps) {
			context.logInfo("Performing step " + step++);
			if (errorAfter > 0 && step % errorAfter == 0) {
				context.logError("A random error occured in step " + step);
			}
			try {
				Thread.sleep(delay);
			}
			catch (InterruptedException e) {
			}
			context.stepCompleted();
		}
	}

	public boolean isConcurrent() {
		return false;
	}
	
	public boolean isRepeatable() {
		return true;
	}

	public void tearDown(String objectId) {
		step = 1;
	}

}
