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
 *   Felix Gnass [fgnass at neteye dot de]
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.riot.job.command;

import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.core.AbstractCommand;
import org.riotfamily.riot.list.command.result.GotoUrlResult;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.riotfamily.riot.runtime.RiotRuntimeAware;

public class JobCommand extends AbstractCommand implements
		RiotRuntimeAware {

	public static final String JOB_STATUS_ACTION = "jobStatus";

	private String jobType;

	private RiotRuntime runtime;

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}

	public void setRiotRuntime(RiotRuntime runtime) {
		this.runtime = runtime;
	}

	public boolean isEnabled(CommandContext context) {
		return true;
	}

	public CommandResult execute(CommandContext context) {
		String objectId = context.getObjectId() != null
				? context.getObjectId() : context.getParentId();

		
		return new GotoUrlResult(context, runtime.getServletPrefix()
				+ "/job?objectId="	+ objectId + "&type=" + jobType);
	}
}
