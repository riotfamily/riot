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
package org.riotfamily.riot.job.ui;

import java.util.Collection;

import org.directwebremoting.WebContextFactory;
import org.riotfamily.riot.job.JobManager;
import org.riotfamily.riot.job.dao.JobDao;
import org.riotfamily.riot.job.model.JobDetail;
import org.riotfamily.riot.job.model.JobLogEntry;

public class JobUIService {

	private JobManager manager;
	
	private JobDao dao;
	
	private JobUIUpdater updater;
	
	public JobUIService(JobManager manager, JobDao dao, JobUIUpdater updater) {
		this.manager = manager;
		this.dao = dao;
		this.updater = updater;
	}
	
	public JobDetail getJobDetail(Long jobId) {
		updater.register(WebContextFactory.get(), jobId);
		return dao.getJobDetail(jobId);
	}
	
	public Collection<JobLogEntry> getLogEntries(Long jobId) {
		return dao.getLogEntries(jobId);
	}
	
	public void executeJob(Long jobId) {
		manager.executeJob(dao.getJobDetail(jobId));
	}
	
	public void interruptJob(Long jobId) {
		manager.interruptJob(dao.getJobDetail(jobId));
	}
	
	public void cancelJob(Long jobId) {
		manager.cancelJob(dao.getJobDetail(jobId));
	}
	
}
