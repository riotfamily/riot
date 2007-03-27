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
package org.riotfamily.components.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.util.UrlPathHelper;

public final class PageRequestUtils {

	private static final String CONTEXT_MAP_ATTRIBUTE =
			PageRequestUtils.class.getName() + ".contextMap";

	private static Log log = LogFactory.getLog(PageRequestUtils.class);
	
	private static UrlPathHelper urlPathHelper = new UrlPathHelper();
	
	private PageRequestUtils() {
	}
	
	public static boolean storeContext(HttpServletRequest request, 
			Object contextKey, int timeToLive) {
		
		if (ComponentEditorRequest.isWrapped(request)) {
			log.debug("Request is already wrapped - ignoring it ...");
			return false;
		}
		else {
			String uri = urlPathHelper.getOriginatingRequestUri(request);
			log.debug("Storing context for URI: " + uri);
			ContextMap contextMap = getContextMap(request);
			PageRequestContext context = new PageRequestContext(request);
			contextMap.put(uri, contextKey, context, timeToLive);
			return true;
		}
	}
	
	public static void touchContext(HttpServletRequest request, String pageUri) {
		ContextMap contextMap = getContextMap(request);
		contextMap.touch(pageUri);
		contextMap.removeExpiredContexts();
	}

	public static PageRequestContext getContext(HttpServletRequest request, 
			String pageUri, Object contextKey) {
		
		ContextMap contexts = getContextMap(request);
		return contexts.get(pageUri, contextKey);
	}
	
	public static HttpServletRequest wrapRequest(
			HttpServletRequest request, String pageUri, Object contextKey) 
			throws RequestContextExpiredException {
		
		ContextMap contexts = getContextMap(request);
		PageRequestContext context = contexts.get(pageUri, contextKey);
		if (context == null) {
			throw new RequestContextExpiredException();
		}
		return new ComponentEditorRequest(request, context);
	}
	
	private static ContextMap getContextMap(HttpServletRequest request) {
		HttpSession session = request.getSession();
		ContextMap contextMap = (ContextMap) session.getAttribute(
				CONTEXT_MAP_ATTRIBUTE);
		
		if (contextMap == null) {
			contextMap = new ContextMap();
			session.setAttribute(CONTEXT_MAP_ATTRIBUTE, contextMap);
		}
		return contextMap;
	}

}
