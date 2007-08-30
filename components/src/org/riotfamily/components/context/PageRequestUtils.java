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
import org.riotfamily.common.web.util.ServletUtils;

public final class PageRequestUtils {

	private static final String CONTEXT_MAP_ATTRIBUTE =
			PageRequestUtils.class.getName() + ".contextMap";

	private static Log log = LogFactory.getLog(PageRequestUtils.class);
	
	private PageRequestUtils() {
	}
	
	public static boolean isPartialRequest(HttpServletRequest request) {
		return PartialPageRequest.isWrapped(request);
	}
	
	public static boolean createAndStoreContext(HttpServletRequest request, 
			Object contextKey, int timeToLive) {
		
		PageRequestContext context = createContext(request, contextKey);
		if (context != null) {
			storeContext(context, request, timeToLive);
			return true;
		}
		return false;
	}
	
	public static PageRequestContext createContext(HttpServletRequest request, 
			Object contextKey) {
		
		if (PartialPageRequest.isWrapped(request, contextKey)) {
			log.debug("Request is already wrapped - ignoring it ...");
			return null;
		}
		return new PageRequestContext(contextKey, request);
	}
	
	public static void storeContext(PageRequestContext context,
			HttpServletRequest request, int timeToLive) {
		
		String uri = ServletUtils.getOriginatingRequestUri(request);
		log.debug("Storing context for " + uri + "#" + context.getKey());
		ContextMap contextMap = getContextMap(request);
		contextMap.put(uri, context.getKey(), context, timeToLive);
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
		
		log.debug("Wrapping context for key " + contextKey);
		ContextMap contexts = getContextMap(request);
		PageRequestContext context = contexts.get(pageUri, contextKey);
		if (context == null) {
			throw new RequestContextExpiredException();
		}
		return new PartialPageRequest(request, context);
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
