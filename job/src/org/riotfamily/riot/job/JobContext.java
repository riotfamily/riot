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
package org.riotfamily.riot.job;



/**
 * Context that is passed to a {@link Job} upon execution.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface JobContext {

	/**
	 * Returns the objectId.
	 */
	public String getObjectId();

	/**
	 * Notifies the DAO that a step has been completed.
	 * @throws JobInterruptedException if the job has been interrupted 
	 */
	public void stepCompleted() throws JobInterruptedException;
			
	/**
	 * Logs an info message.
	 */
	public void logInfo(String message);
	
	/**
	 * Logs an error message.
	 */
	public void logError(String message);
	
	/**
	 * Changes the job's description.
	 */
	public void updateDescription(String description);
	
	/**
	 * Changes the number of total steps.
	 */
	public void updateStepsTotal(int stepsTotal, boolean resetStepsCompleted);
			
}
