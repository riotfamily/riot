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
package org.riotfamily.statistics.commands;

import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandResult;
import org.riotfamily.core.screen.list.command.SelectionItem;
import org.riotfamily.core.screen.list.command.impl.support.AbstractChildCommand;
import org.riotfamily.core.screen.list.command.result.BatchResult;
import org.riotfamily.core.screen.list.command.result.NotificationResult;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public class RefreshListCommand extends AbstractChildCommand {

	@Override
	protected String getIcon() {
		return "arrow_refresh";
	}
	
	@Override
	protected String getAction() {
		return null;
	}
	
	@Override
	protected CommandResult execute(CommandContext context, SelectionItem parent) {
		return new BatchResult(
				new RefreshListResult(),
				new NotificationResult(context, this)
						.setDefaultMessage("The list has been refreshed."));
	}

}
