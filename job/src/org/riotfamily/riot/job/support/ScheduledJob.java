/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is Riot.
 *
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
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
		if (jobDetail.getState() == JobDetail.INITIALIZED) {
			jobManager.executeJob(jobDetail);
		}
	}
	
}
