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
package org.riotfamily.riot.job.context;

import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.JobInterruptedException;
import org.riotfamily.riot.job.support.JobTask;

public class TaskJobContext implements JobContext {
	
	private JobTask task;
	
	public TaskJobContext(JobTask task) {
		this.task = task;
	}

	/**
	 * Returns the objectId.
	 */
	public String getObjectId() {
		return task.getDetail().getObjectId();
	}

	/**
	 * Notifies the DAO that a step has been completed.
	 * @throws JobInterruptedException if the job has been interrupted 
	 */
	public void stepCompleted() throws JobInterruptedException {
		task.stepCompleted();	
	}
			
	/**
	 * Logs an info message.
	 */
	public void logInfo(String message) {
		task.logInfo(message);
	}
	
	/**
	 * Logs an error message.
	 */
	public void logError(String message) {
		task.logError(message);
	}
	
	public void updateDescription(String description) {
		task.updateDescription(description);
	}
	
	public void updateStepsTotal(int stepsTotal, boolean resetStepsCompleted) {
		task.updateStepsTotal(stepsTotal);
		if (resetStepsCompleted) {
			task.getDetail().setStepsCompleted(0);
		}
	}

}
