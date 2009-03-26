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
package org.riotfamily.common.web.mapping;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.SpringUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.web.servlet.support.RequestContextUtils;

public final class HandlerUrlUtils {

	private HandlerUrlUtils() {
	}
	
	public static HandlerUrlResolver getUrlResolver(HttpServletRequest request) {
		return getUrlResolver(RequestContextUtils.getWebApplicationContext(request));
	}
	
	public static HandlerUrlResolver getUrlResolver(BeanFactory lbf) {
		if (lbf.containsBean("handlerUrlResolver")) {
			return SpringUtils.getBean(lbf,	"handlerUrlResolver", 
					HandlerUrlResolver.class);
		}
		return SpringUtils.beanOfType(lbf, HandlerUrlResolver.class);
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 * @param request The current request
	 */
	public static String getUrl(HttpServletRequest request,
			String handlerName, Object attributes) {
		
		return getUrlResolver(request).getUrlForHandler(
				request, handlerName, attributes);
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param attributes Optional attributes to fill out wildcards.
	 * @param request The current request
	 */
	public static String getContextRelativeUrl(HttpServletRequest request, 
			String handlerName, Object... attributes) {
		
		return request.getContextPath() + getUrlResolver(request)
				.getUrlForHandler(request, handlerName, attributes);
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param attributes Optional attributes to fill out wildcards. Can either 
	 * 		  be <code>null</code>, a primitive wrapper, a Map or a bean.
	 * @param request The current request
	 */
	public static String getContextRelativeUrl(HttpServletRequest request,
			String handlerName, Object attributes) {
		
		return request.getContextPath() + getUrlResolver(request)
				.getUrlForHandler(request, handlerName, attributes);
	}
	
	/**
	 * Returns the URL of a mapped handler.
	 * @param handlerName The name of the handler
	 * @param attributes Optional attributes to fill out wildcards.
	 * @param request The current request
	 */
	public static String getUrl(HttpServletRequest request, 
			String handlerName, Object... attributes) {
		
		return getUrlResolver(request).getUrlForHandler(
				request, handlerName, attributes);
	}
	
	/**
	 * Sends a redirect to the handler with the specified name.
	 * @param request The current request
	 * @param response The current response
	 * @param handlerName The name of the handler
	 * @throws IOException
	 */
	public static void sendRedirect(HttpServletRequest request, 
			HttpServletResponse response, String handlerName) 
			throws IOException {
		
		String url = getUrl(request, handlerName);
		ServletUtils.resolveAndRedirect(request, response, url);
	}
	
}
