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
package org.riotfamily.core.security.session;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.filter.FilterPlugin;
import org.riotfamily.core.security.auth.RiotUser;

/**
 * Servlet filter that binds the authenticated user (if present) to the
 * current thread. 
 * 
 * @see LoginManager#getUser(HttpServletRequest)
 * @see SecurityContext#bindUserToCurrentThread(RiotUser)
 */
public final class AccessControlFilterPlugin extends FilterPlugin {

	public AccessControlFilterPlugin() {
		setOrder(0);
	}

	public void doFilter(HttpServletRequest request,
		HttpServletResponse response, FilterChain filterChain)
		throws IOException, ServletException {
		
		try {
			LoginManager loginManager = LoginManager.getInstance(getServletContext());
			RiotUser user = loginManager.getUser(request);
			SecurityContext.bindUserToCurrentThread(user);
			filterChain.doFilter(request, response);
		}
		finally {
			SecurityContext.resetUser();
		}
	}
}
