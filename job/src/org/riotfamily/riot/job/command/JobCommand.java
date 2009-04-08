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

import java.util.Map;

import org.riotfamily.common.util.Generics;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;

public class JobCommand extends AbstractCommand {

	private String jobType;

	public void setJobType(String jobType) {
		this.jobType = jobType;
	}
	
	@Override
	protected String getAction(CommandContext context) {
		return jobType;
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		/*
		String objectId = selection.getSingleObjectId() != null
				? selection.getSingleObjectId() 
				: context.getParentId();

		Map<String, String> attributes = Generics.newHashMap();
		attributes.put("type", jobType);
		attributes.put("objectId", objectId);
		//String url = getRuntime().getUrlForHandler("jobUIController", attributes);
		return new GotoUrlResult(context, ServletUtils.addParameter(url, 
				"title", getLabel(context.getMessageResolver())));
		*/
		return null;
	}
}
