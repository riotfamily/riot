package org.riotfamily.cachius.support;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Utility class for working with URL-based session tracking.
 */
public final class SessionUtils {

	private static final String INITIAL_SESSION_STATE = 
			SessionUtils.class.getName() + ".initialState";

	private SessionUtils() {
	}
	
	/**
     * Returns whether URLs would be encoded by the servlet container if
     * {@link HttpServletResponse#encodeURL(java.lang.String) 
     * response.encodeURL()} was called.
     * 
     * Since the session state may change during a request (due to parallel
     * request caused by embedded resources) the initial state is stored as
     * request attribute.
     */
    public static boolean urlsNeedEncoding(HttpServletRequest request) {
        boolean encodeUrls = urlsCurrentlyNeedEncoding(request);
    	Boolean initialState = (Boolean) request.getAttribute(INITIAL_SESSION_STATE);
    	if (initialState == null) {
    		initialState = Boolean.valueOf(encodeUrls);
            request.setAttribute(INITIAL_SESSION_STATE, initialState);
    	}
    	return encodeUrls;
    }
    
    public static boolean sessionStateChanged(HttpServletRequest request) {
    	Boolean initialState = (Boolean) request.getAttribute(INITIAL_SESSION_STATE);
    	if (initialState == null) {
    		throw new IllegalStateException("urlsNeedEncoding() must be called first.");
    	}
    	return urlsCurrentlyNeedEncoding(request) != initialState.booleanValue();
    }
    
    public static void addStateToCacheKey(HttpServletRequest request, 
    		StringBuffer key) {
    	
    	if (urlsNeedEncoding(request)) {
			key.append(";jsessionid");
		}
    }
    
    private static boolean urlsCurrentlyNeedEncoding(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session == null) {
			return false;
		}        
        return  session.isNew() || request.isRequestedSessionIdFromURL();
	}

}
