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
package org.riotfamily.riot.job.dao;

import java.util.Collection;

import org.riotfamily.riot.job.model.JobDetail;
import org.riotfamily.riot.job.model.JobLogEntry;


public interface JobDao {

	public Collection<JobLogEntry> getLogEntries(Long jobId);
	
	public void log(JobLogEntry entry);
	
	public Collection<JobDetail> getJobDetails();
	
	public Collection<JobDetail> getPendingJobDetails();
	
	public JobDetail getJobDetail(Long jobId);
	
	public JobDetail getPendingJobDetail(String type, String objectId);
	
	public JobDetail getLastCompletedJobDetail(String type, String objectId);
	
	public int getAverageStepTime(String type);
	
	public void saveJobDetail(JobDetail detail);
	
	public void updateJobDetail(JobDetail detail);

	public void deleteObsoleteJobDetails();

}
