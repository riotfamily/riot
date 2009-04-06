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
package org.riotfamily.core.screen.list.command;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.util.StringUtils;

public abstract class AbstractCommand implements Command {

	private static final String COMMAND_NAME_SUFFIX = "Command";
	
	private String action;
	
	public boolean isEnabled(CommandContext context, Selection selection) {
		return true;
	}
	
	public CommandInfo getInfo(CommandContext context) {
		CommandInfo info = new CommandInfo();
		info.setLabel(FormatUtils.xmlToTitleCase(getAction(context)));
		info.setStyleClass(getStyleClass(context));
		info.setShowOnForm(isShowOnForm(context));
		return info;
	}
	
	protected String getAction(CommandContext context) {
		if (action == null) {
			action = getClass().getName();
			int i = action.lastIndexOf('.');
			if (i >= 0) {
				action = action.substring(i + 1);
			}
			if (action.endsWith(COMMAND_NAME_SUFFIX)) {
				action = action.substring(0, action.length() - COMMAND_NAME_SUFFIX.length());
			}
			if (action.contains("$")) {
				action = action.substring(action.indexOf('$') + 1);
			}
			action = StringUtils.uncapitalize(action);
		}
		return action;
	}
	
	protected String getStyleClass(CommandContext context) {
		return getAction(context);
	}
	
	protected boolean isShowOnForm(CommandContext context) {
		return false;
	}

}
