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
 *   Jan-Frederic Linde [jfl at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.job.support;

import java.util.List;

import org.riotfamily.riot.job.Job;
import org.riotfamily.riot.job.JobContext;
import org.riotfamily.riot.job.context.CommonsLogginJobContext;
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
		JobContext context = new CommonsLogginJobContext();
		for (Job job : jobs) {
			job.execute(context);
		}
	}

}
