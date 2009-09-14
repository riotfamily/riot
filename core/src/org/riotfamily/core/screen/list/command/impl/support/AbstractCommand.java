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
package org.riotfamily.core.screen.list.command.impl.support;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.core.screen.list.command.Command;
import org.riotfamily.core.screen.list.command.CommandContext;
import org.riotfamily.core.screen.list.command.CommandInfo;
import org.riotfamily.core.screen.list.command.Selection;
import org.springframework.util.StringUtils;

public abstract class AbstractCommand implements Command {

	private static final String COMMAND_NAME_SUFFIX = "Command";
	
	private String name;
	
	public boolean isEnabled(CommandContext context, Selection selection) {
		return true;
	}
	
	public CommandInfo getInfo(CommandContext context) {
		String action = getAction(context);
		if (action == null) {
			return null;
		}
		return new CommandInfo(
				action,
				getLabel(context, action),
				getIconUrl(context, getIcon(action)),
				isShowOnForm(context));
	}
	
	protected String getName() {
		if (name == null) {
			name = getClass().getName();
			int i = name.lastIndexOf('.');
			if (i >= 0) {
				name = name.substring(i + 1);
			}
			if (name.endsWith(COMMAND_NAME_SUFFIX)) {
				name = name.substring(0, name.length() - COMMAND_NAME_SUFFIX.length());
			}
			if (name.contains("$")) {
				name = name.substring(name.indexOf('$') + 1);
			}
			name = StringUtils.uncapitalize(name);
		}
		return name;
	}
	
	protected String getAction(CommandContext context) {
		return getName();
	}
	
	protected String getLabel(CommandContext context, String action) {
		return context.getMessageResolver().getMessage(
				"command." + action + ".label", 
				FormatUtils.xmlToTitleCase(action));
	}
		
	protected String getIconUrl(CommandContext context, String iconName) {
		return context.getResourcePath() + "style/images/icons/" 
				+ iconName + ".png";
	}
	
	protected String getIcon(String action) {
		return action;
	}
	
	protected boolean isShowOnForm(CommandContext context) {
		return false;
	}

}
