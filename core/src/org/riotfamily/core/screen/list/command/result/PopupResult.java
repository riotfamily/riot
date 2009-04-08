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
package org.riotfamily.core.screen.list.command.result;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteProperty;
import org.riotfamily.core.screen.list.command.CommandResult;


@DataTransferObject
public class PopupResult implements CommandResult {

	private String url;
	
	private String windowName;
	
	private String arguments;
	
	private String popupBlockerMessage;
	
	
	public PopupResult(String url) {
		this.url = url;
	}
	
	@RemoteProperty
	public String getAction() {
		return "popup";
	}
	
	@RemoteProperty
	public String getPopupBlockerMessage() {
		return this.popupBlockerMessage;
	}

	public PopupResult setPopupBlockerMessage(String popupBlockerMessage) {
		this.popupBlockerMessage = popupBlockerMessage;
		return this;
	}

	@RemoteProperty
	public String getUrl() {
		return this.url;
	}

	@RemoteProperty
	public String getWindowName() {
		return this.windowName;
	}

	public PopupResult setWindowName(String windowName) {
		this.windowName = windowName;
		return this;
	}
	
	@RemoteProperty
	public String getArguments() {
		return arguments;
	}
	
	public PopupResult setArguments(String arguments) {
		this.arguments = arguments;
		return this;
	}
		
}
