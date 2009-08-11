package org.riotfamily.common.view;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.Generics;

/**
 * Class that temporarily stores data in the HTTP session to pass it from one 
 * request to another. Used by the {@link FlashScopeView} and its subclasses
 * to pass on data during redirects.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class FlashScope {

	private static final String SESSION_ATTRIBUTE = FlashScope.class.getName();

	private Map<String, Map<String, ?>> models = Generics.newHashMap();
		
	/**
	 * Stores a model map in the HTTP session for later retrieval by the next 
	 * request. 
	 * @param request The current request
	 * @param model The model to store
	 * @param url The URL of the next request  
	 */
	@SuppressWarnings("unchecked")
	public static void store(HttpServletRequest request, Map<?, ?> model, String url) {
		FlashScope flashScope = getFlashScope(request, true);
		flashScope.models.put(url, (Map<String, ?>) model);
	}
	
	static void expose(HttpServletRequest request) {
		FlashScope flashScope = getFlashScope(request, false);
		if (flashScope != null) {
			String url = ServletUtils.getRequestUrlWithQueryString(request);
			Map<String, ?> model = flashScope.models.remove(url);
			if (model != null) {
				for (Map.Entry<String, ?> entry : model.entrySet()) {
					request.setAttribute(entry.getKey(), entry.getValue());
				}
			}
		}
	}

	private static FlashScope getFlashScope(HttpServletRequest request, boolean create) {
		FlashScope flashScope = null;
		HttpSession session = request.getSession(create);
		if (session != null) {
			flashScope = (FlashScope) session.getAttribute(SESSION_ATTRIBUTE);
			if (flashScope == null && create) {
				flashScope = new FlashScope();
				session.setAttribute(SESSION_ATTRIBUTE, flashScope);
			}
		}
		return flashScope;
	}

	
	
}
