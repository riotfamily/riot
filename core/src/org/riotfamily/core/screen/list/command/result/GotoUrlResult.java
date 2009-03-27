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

import javax.servlet.http.HttpServletRequest;

import org.directwebremoting.annotations.DataTransferObject;
import org.directwebremoting.annotations.RemoteMethod;

@DataTransferObject
public class GotoUrlResult implements CommandResult {

	private String url;
	
	private String target = "self";
	
	private boolean replace;
	
	public GotoUrlResult(String url) {
		this.url = url;
	}
	
	public GotoUrlResult(HttpServletRequest request, String url) {
		this.url = request.getContextPath() + url;
	}
	
	@RemoteMethod
	public String getAction() {
		return "gotoUrl";
	}

	@RemoteMethod
	public boolean isReplace() {
		return this.replace;
	}

	public void setReplace(boolean replace) {
		this.replace = replace;
	}

	@RemoteMethod
	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	@RemoteMethod
	public String getUrl() {
		return this.url;
	}

}
