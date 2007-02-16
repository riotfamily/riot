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
package org.riotfamily.riot.list.command.result;

import org.riotfamily.riot.list.command.CommandResult;

public class PopupResult implements CommandResult {

	public static final String ACTION = "popup";
	
	private String url;
	
	private String windowName;
	
	private String popupBlockerMessage;
	
	
	public PopupResult(String url) {
		this.url = url;
	}
	
	public String getAction() {
		return ACTION;
	}
	
	public PopupResult(String url, String windowName, 
			String popupBlockerMessage) {
		
		this.url = url;
		this.windowName = windowName;
		this.popupBlockerMessage = popupBlockerMessage;
	}

	public String getPopupBlockerMessage() {
		return this.popupBlockerMessage;
	}

	public void setPopupBlockerMessage(String popupBlockerMessage) {
		this.popupBlockerMessage = popupBlockerMessage;
	}

	public String getUrl() {
		return this.url;
	}

	public String getWindowName() {
		return this.windowName;
	}

	public void setWindowName(String windowName) {
		this.windowName = windowName;
	}
	
}
