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
package org.riotfamily.riot.list.command.result;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.riot.list.command.CommandResult;

public class GotoUrlResult implements CommandResult {

	private String url;
	
	private String target = "window";
	
	private boolean replace;
	
	private boolean contextRelative;

	public GotoUrlResult(String url) {
		this(url, true);
	}
	
	public GotoUrlResult(String url, boolean contextRelative) {
		this.url = url;
		this.contextRelative = contextRelative;
	}

	public String getJavaScriptCode(HttpServletRequest request, 
			HttpServletResponse response) {
		
		StringBuffer js = new StringBuffer();
		js.append(target);
		js.append(".location.");
		js.append(replace ? "replace('" : "href = '");
		String href = contextRelative ? request.getContextPath() + url : url;  
		js.append(response.encodeURL(href));
		js.append('\'');
		if (replace) {
			js.append(')');
		}
		return js.toString();
	}
}
