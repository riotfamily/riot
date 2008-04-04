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
package org.riotfamily.riot.runtime.macro;

import java.io.IOException;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.riot.runtime.RiotRuntime;
import org.riotfamily.riot.security.AccessController;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class RiotMacroHelper {

	private RiotRuntime runtime;
	
	private HttpServletRequest request;

	private HttpServletResponse response;
	
	public RiotMacroHelper(RiotRuntime runtime, HttpServletRequest request, HttpServletResponse response) {
		this.runtime = runtime;
		this.request = request;
		this.response = response;
	}

	public boolean isAuthenticatedUser() {
		return AccessController.isAuthenticatedUser();
	}

	public RiotRuntime getRuntime() {
		return this.runtime;
	}
	
	public String resolveAndEncodeUrl(String url) {
		return ServletUtils.resolveAndEncodeUrl(url, request, response);
	}
	
	public String include(String url) throws ServletException, IOException {
		request.getRequestDispatcher(url).include(request, response);
		return "";
	}	
	
}
