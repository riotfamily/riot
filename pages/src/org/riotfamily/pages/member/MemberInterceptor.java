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
package org.riotfamily.pages.member;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.OncePerRequestInterceptor;
import org.riotfamily.pages.page.Page;
import org.riotfamily.pages.page.support.PageUtils;

public class MemberInterceptor extends OncePerRequestInterceptor
		implements MemberBinderAware {

	public static final String INTERCEPTED_URL = 
			MemberInterceptor.class.getName() + ".interceptedUrl"; 
	
	private String loginUrl;
	
	private MemberBinder memberBinder;
	
	public void setLoginUrl(String loginUrl) {
		this.loginUrl = loginUrl;
	}

	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}

	protected boolean preHandleOnce(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
	
		Page page = PageUtils.getPage(request);
		WebsiteMember member = memberBinder.getMember(request);
		if (!page.isAccessible(request, member)) {
			if (loginUrl != null) {
				String url = request.getRequestURI();
				request.getSession().setAttribute(INTERCEPTED_URL, url);
				response.sendRedirect(request.getContextPath() + loginUrl);
			}
			else {
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
			}
			return false;
		}
		return true;
	}

}
