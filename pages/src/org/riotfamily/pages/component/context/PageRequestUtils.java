package org.riotfamily.pages.component.context;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.common.web.util.ServletMappingHelper;

public final class PageRequestUtils {

	private static final String CONTEXT_MAP_ATTRIBUTE =
			PageRequestUtils.class.getName() + ".contextMap";

	private static Log log = LogFactory.getLog(PageRequestUtils.class);
	
	private static ServletMappingHelper servletMappingHelper = 
			new ServletMappingHelper(true);
	
	private PageRequestUtils() {
	}
	
	public static void storeContext(HttpServletRequest request, int timeToLive) {
		String uri = servletMappingHelper.getRequestUri(request);
		log.debug("Storing context for URI: " + uri);
		ContextMap contextMap = getContextMap(request);
		PageRequestContext context = new PageRequestContext(request, timeToLive);
		contextMap.put(uri, context);
	}
	
	public static void touchContext(HttpServletRequest request, String uri) {
		ContextMap contextMap = getContextMap(request);
		PageRequestContext context = contextMap.get(uri);
		if (context != null) {
			context.touch();
		}
		contextMap.removeExpiredContexts();
	}

	public static PageRequestContext getContext(HttpServletRequest request, String uri) {
		ContextMap contexts = getContextMap(request);
		return contexts.get(uri);
	}
	
	public static HttpServletRequest wrapRequest(
			HttpServletRequest request, String uri) 
			throws RequestContextExpiredException {
		
		ContextMap contexts = getContextMap(request);
		PageRequestContext context = contexts.get(uri);
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
