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
package org.riotfamily.riot.security.session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.interceptor.OncePerRequestInterceptor;
import org.riotfamily.riot.security.auth.RiotUser;

/**
 * HandlerInterceptor that binds the authenticated user (if present) to the
 * current thread. 
 * 
 * @see AccessController
 */
public class AccessControlInterceptor extends OncePerRequestInterceptor {

	public boolean preHandleOnce(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		
		RiotUser user = LoginManager.getUser(request);
		SecurityContext.bindUserToCurrentThread(user);
		return isAuthorized(request, response, user);
	}

	protected boolean isAuthorized(HttpServletRequest request,
			HttpServletResponse response, RiotUser user) throws Exception {
		
		return true;
	}
	
	public final void afterLastCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {

		SecurityContext.resetUser();
	}

}
