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
package org.riotfamily.riot.security.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.riot.security.PrincipalBinder;

public class HttpSessionPrincipalBinder implements PrincipalBinder {

	public static final String DEFAULT_SESSION_KEY = "riotSubject";
	
	private String sessionKey = DEFAULT_SESSION_KEY;
	
	
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}
	
	protected String getSessionKey() {
		return sessionKey;
	}

	public String getPrincipal(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return null;
		}
		return (String) session.getAttribute(sessionKey);
	}

	public void bindPrincipalToRequest(String principal, 
			HttpServletRequest request) {
		
		request.getSession().setAttribute(sessionKey, principal);
	}

	public void unbind(HttpServletRequest request, 
			HttpServletResponse response) {
		
		request.getSession().removeAttribute(sessionKey);		
	}

}
