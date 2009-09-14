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
