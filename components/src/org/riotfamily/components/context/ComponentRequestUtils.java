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

public final class ComponentRequestUtils {

	private static final String CONTEXT_MAP_ATTRIBUTE =
			ComponentRequestUtils.class.getName() + ".contextMap";

	private static Log log = LogFactory.getLog(ComponentRequestUtils.class);
	
	private ComponentRequestUtils() {
	}
	
	public static boolean isComponentRequest(HttpServletRequest request) {
		return ComponentRequest.isWrapped(request);
	}
	
	public static ComponentListRequestContext createContext(HttpServletRequest request, 
			Long listId) {
		
		if (ComponentRequest.isWrapped(request, listId)) {
			log.debug("Request is already wrapped - ignoring it ...");
			return null;
		}
		return new ComponentListRequestContext(listId, request);
	}
	
	public static void storeContext(ComponentListRequestContext context,
			HttpServletRequest request, int timeToLive) {
		
		String uri = ServletUtils.getOriginatingRequestUri(request);
		log.debug("Storing context for " + uri + "#" + context.getListId());
		ContextMap contextMap = getContextMap(request);
		contextMap.put(uri, context.getListId(), context, timeToLive);
	}
	
	public static void touchContext(HttpServletRequest request, String pageUri) {
		ContextMap contextMap = getContextMap(request);
		contextMap.touch(pageUri);
		contextMap.removeExpiredContexts();
	}

	public static ComponentListRequestContext getContext(HttpServletRequest request, 
			String pageUri, Long listId) {
		
		ContextMap contexts = getContextMap(request);
		return contexts.get(pageUri, listId);
	}
	
	public static HttpServletRequest wrapRequest(
			HttpServletRequest request, String pageUri, Long listId) 
			throws RequestContextExpiredException {
		
		log.debug("Wrapping context for list " + listId);
		ComponentListRequestContext context = getContext(request, pageUri, listId);
		if (context == null) {
			throw new RequestContextExpiredException(pageUri, listId);
		}
		return new ComponentRequest(request, context);
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
