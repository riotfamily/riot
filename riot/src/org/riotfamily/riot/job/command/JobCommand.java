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
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.job.command;

import org.riotfamily.riot.job.JobManager;
import org.riotfamily.riot.job.persistence.JobDetail;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;
import org.riotfamily.riot.list.ui.render.RenderContext;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class JobCommand extends AbstractCommand implements 
		ApplicationContextAware {

	public static final String JOB_STATUS_ACTION = "jobStatus";
	
	private JobManager jobManager;
	
	private String jobType;
	
	private RiotRuntime runtime;
	
	public void setJobManager(JobManager jobManager) {
		this.jobManager = jobManager;
	}

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	public void setApplicationContext(ApplicationContext context) {
		runtime = (RiotRuntime) BeanFactoryUtils.beanOfTypeIncludingAncestors(
				context, RiotRuntime.class);
	}
	
	public boolean isEnabled(RenderContext context) {
		return true;
	}
	
	public CommandResult execute(CommandContext context) {
		String objectId = context.getObjectId() != null 
				? context.getObjectId() : context.getParentId();
				
		JobDetail detail = jobManager.getOrCreateJob(jobType, objectId);
		return new GotoUrlResult(runtime.getServletPrefix() + "/job?jobId=" 
				+ detail.getId());
	}
}
