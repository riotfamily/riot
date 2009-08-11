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

import org.riotfamily.common.scheduling.ScheduledTaskSupport;
import org.riotfamily.riot.job.JobManager;
import org.riotfamily.riot.job.model.JobDetail;
import org.springframework.beans.factory.annotation.Required;

public class ScheduledJob extends ScheduledTaskSupport {

	private JobManager jobManager;
	
	private String jobType;

	@Required
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}

	@Required
	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public void execute() throws Exception {
		JobDetail jobDetail = jobManager.getOrCreateJob(jobType, null, false);
		if (jobDetail.getState() == JobDetail.INITIALIZED
				|| jobDetail.getState() == JobDetail.INTERRUPTED) {
			
			jobManager.executeJob(jobDetail);
		}
	}
	
}
