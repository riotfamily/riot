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
 * Interface to define background jobs.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public interface Job {

	/**
	 * Creates a JobDescription and optionally performs setup tasks.
	 */
	public JobDescription setup(String objectId) throws JobCreationException;
	
	/**
	 * Returns whether the job can be executed concurrently with different 
	 * objectIds.
	 */
	public boolean isConcurrent();
	
	/**
	 * Returns whether the job can be executed more than once with the same
	 * objectId.  
	 */
	public boolean isRepeatable();
	
	/**
	 * Performs the actual work. The given context can be used to log messages
	 * or to provide progress information.
	 */
	public void execute(JobContext context) throws Exception;

	/**
	 * Invoked when a job is canceled or completed.
	 */
	public void tearDown(String objectId);

}
