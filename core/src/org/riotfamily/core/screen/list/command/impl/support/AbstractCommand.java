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
		return new CommandInfo(
				action,
				getLabel(context, action),
				getStyleClass(context, action),
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
		
	protected String getStyleClass(CommandContext context, String action) {
		return FormatUtils.toCssClass(action);
	}
	
	protected boolean isShowOnForm(CommandContext context) {
		return false;
	}

}
