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

import java.util.List;

import org.riotfamily.riot.job.Job;
import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.context.RiotLogJobContext;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

public class SetupJobs implements InitializingBean {
	
	private List<Job> jobs;
	
	public void setJobs(List<Job> jobs) {
		this.jobs = jobs;
	}
	
	@Transactional
	public void afterPropertiesSet() throws Exception {		
		Assert.notNull(jobs, "Jobs must not be null");
		JobContext context = new RiotLogJobContext();
		for (Job job : jobs) {
			job.execute(context);
		}
	}

}
