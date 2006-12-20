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
package org.riotfamily.riot.list.command.core;

import org.riotfamily.common.util.PropertyUtils;
import org.riotfamily.riot.form.command.FormCommand;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.PopupResult;
import org.riotfamily.riot.list.command.support.AbstractCommand;

/**
 * @author Felix Gnass <fgnass@neteye.de>
 * @since 6.4
 */
public class LinkCommand extends AbstractCommand implements FormCommand {

	private static final String ACTION = "link";
	
	private String link;
	
	private boolean contextRelative = true;
	
	private String windowName;
	
	public void setLink(String link) {
		this.link = link.replace('@', '$');
	}

	public void setContextRelative(boolean contextRelative) {
		this.contextRelative = contextRelative;
	}

	public void setWindowName(String windowName) {
		this.windowName = windowName;
	}
	
	public boolean isEnabled(CommandContext context) {
		return context.getObjectId() != null;
	}

	public CommandResult execute(CommandContext context) {
		String url = PropertyUtils.evaluate(link, context.getBean());
		if (contextRelative) {
			url = context.getRequest().getContextPath() + url;
		}
		return new PopupResult(url, windowName,	
				getPopupBlockerMessage(context, url));
	}
	
	protected String getPopupBlockerMessage(CommandContext context, String url) {
		return context.getMessageResolver().getMessage("error.popupBlocked", 
				new Object[] { url }, 
				"A popup-blocker prevented the window from opening. " +
				"Please allow popups for this domain.");
	}

	public String getAction(CommandContext context) {
		return ACTION;
	}

}
