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
package org.riotfamily.components.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.components.editor.EditModeUtils;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class StoreContextInterceptor implements HandlerInterceptor {

	private static final String HANDLED_ATTRIBUTE = 
			StoreContextInterceptor.class.getName() + ".handled";
	
	private static final String CONTEXT_ATTRIBUTE = 
			StoreContextInterceptor.class.getName() + ".context";
	
	private static final String STORE_ATTRIBUTE = 
			StoreContextInterceptor.class.getName() + ".storeContext";
	
	public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler) throws Exception {
		
		if (WebUtils.isIncludeRequest(request) && EditModeUtils.isEditMode(request)) {
			String uri = ServletUtils.getPathWithinApplication(request);
			PageRequestContext context = PageRequestUtils.createContext(request, uri);
			request.setAttribute(CONTEXT_ATTRIBUTE, context);
		}
		request.setAttribute(HANDLED_ATTRIBUTE, Boolean.TRUE);
		return true;
	}

	public void postHandle(HttpServletRequest request, 
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
	}
	
	public void afterCompletion(HttpServletRequest request, 
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		
		if (request.getAttribute(STORE_ATTRIBUTE) != null) {
			PageRequestContext context = (PageRequestContext) 
					request.getAttribute(CONTEXT_ATTRIBUTE);
			
			PageRequestUtils.storeContext(context, request, 120000);
		}
	}
	
	public static void storeContext(HttpServletRequest request) {
		Assert.notNull(request.getAttribute(HANDLED_ATTRIBUTE), 
				"No context found in request. Make sure you add a " 
				+ StoreContextInterceptor.class.getName() 
				+ " to your HandlerMapping(s).");
		
		request.setAttribute(STORE_ATTRIBUTE, Boolean.TRUE);
	}

}
