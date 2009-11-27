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
import org.riotfamily.core.screen.list.command.Selection;
import org.riotfamily.core.screen.list.command.impl.support.AbstractCommand;
import org.riotfamily.core.screen.list.command.result.RefreshListResult;

public abstract class AbstractSwitchCommand extends AbstractCommand {

	private boolean enablingCommand;
	
	public void setEnablingCommand(boolean enablingCommand) {
		this.enablingCommand = enablingCommand;
	}
	
	protected boolean isEnablingCommand() {
		return enablingCommand;
	}

	@Override
	protected String getName() {
		return isEnablingCommand() ? "enable" : "disable";
	}
	
	@Override
	protected String getIcon() {
		return isEnablingCommand() ? "start" : "stop";
	}

	@Override
	protected String getAction() {
		return "admin";
	}
	
	@Override
	public boolean isEnabled(CommandContext context, Selection selection) {
		return isEnablingCommand() ^ isEnabled();
	}
	
	public CommandResult execute(CommandContext context, Selection selection) {
		setEnabled(!isEnabled());
		return new RefreshListResult();
	}

	protected abstract boolean isEnabled();

	protected abstract void setEnabled(boolean enabled);

}
