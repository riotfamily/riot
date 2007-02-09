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
package org.riotfamily.riot.list.command.support;

import org.riotfamily.riot.form.command.FormCommand;
import org.riotfamily.riot.list.command.CommandContext;
import org.riotfamily.riot.list.command.CommandResult;
import org.riotfamily.riot.list.command.result.PopupResult;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public abstract class PopupCommand extends AbstractCommand implements FormCommand {

	private String windowName;
	
	public void setWindowName(String windowName) {
		this.windowName = windowName;
	}
	
	public boolean isEnabled(CommandContext context) {
		return context.getObjectId() != null;
	}

	public CommandResult execute(CommandContext context) {
		String url = getUrl(context);
		return new PopupResult(url, windowName,	
				getPopupBlockerMessage(context, url));
	}
	
	protected String getPopupBlockerMessage(CommandContext context, String url) {
		return context.getMessageResolver().getMessage("error.popupBlocked", 
				new Object[] { url }, 
				"A popup-blocker prevented the window from opening. " +
				"Please allow popups for this domain.");
	}

	protected abstract String getUrl(CommandContext context);

}
