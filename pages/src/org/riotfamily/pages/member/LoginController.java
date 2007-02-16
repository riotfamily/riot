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
package org.riotfamily.pages.member;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.collection.FlatMap;
import org.riotfamily.common.web.view.RedirectView;
import org.springframework.util.Assert;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

public class LoginController implements Controller, MemberBinderAware {

	public static final String USERNAME_PARAM = "username";
	
	public static final String PASSWORD_PARAM = "password";
	
	public static final String SUCCESS_URL_PARAM = "successUrl";
	
	private AuthenticationService authenticationService;
	
	private MemberBinder memberBinder;
	
	private String defaultSucessUrl;
	
	private String loginFormView;
	
	public void setAuthenticationService(AuthenticationService authService) {
		this.authenticationService = authService;
	}

	public void setMemberBinder(MemberBinder memberBinder) {
		this.memberBinder = memberBinder;
	}

	public void setDefaultSucessUrl(String welcomeUrl) {
		this.defaultSucessUrl = welcomeUrl;
	}

	public void setLoginFormView(String loginFormView) {
		this.loginFormView = loginFormView;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		String username = request.getParameter(USERNAME_PARAM);
		String password = request.getParameter(PASSWORD_PARAM);
		
		String successUrl = (String) request.getSession().getAttribute(
				MemberInterceptor.INTERCEPTED_URL);
				
		if (successUrl == null) {
			successUrl = ServletRequestUtils.getStringParameter(request, 
					SUCCESS_URL_PARAM, defaultSucessUrl);
		}

		Map model = new FlatMap();
		model.put("successUrl", successUrl);
		
		if (username != null) {
			WebsiteMember member = authenticationService.authenticate(
					username, password);
			
			memberBinder.bind(member, request);
			if (member != null) {
				return getSuccessModelAndView(member, successUrl, request);
			}
			model.put("username", username);
		}
		
		Assert.notNull(loginFormView);
		return new ModelAndView(loginFormView, model);
	}
	
	protected ModelAndView getSuccessModelAndView(WebsiteMember member, 
			String successUrl, HttpServletRequest request) {
		
		return new ModelAndView(new RedirectView(successUrl));
	}
}
