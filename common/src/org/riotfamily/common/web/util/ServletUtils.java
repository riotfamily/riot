package org.riotfamily.common.web.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class ServletUtils {

	public static final String INCLUDE_URI_REQUEST_ATTRIBUTE = 
			"javax.servlet.include.request_uri";

	public static final String REQUESTED_WITH_HEADER = "X-Requested-With";
	
	public static final String XML_HTTP_REQUEST = "XMLHttpRequest";
	
	/** <p>Valid characters in a scheme.</p>
     *  <p>RFC 1738 says the following:</p>
     *  <blockquote>
     *   Scheme names consist of a sequence of characters. The lower
     *   case letters "a"--"z", digits, and the characters plus ("+"),
     *   period ("."), and hyphen ("-") are allowed. For resiliency,
     *   programs interpreting URLs should treat upper case letters as
     *   equivalent to lower case in scheme names (e.g., allow "HTTP" as
     *   well as "http").
     *  </blockquote>
     * <p>We treat as absolute any URL that begins with such a scheme name,
     * followed by a colon.</p>
     */
    public static final String VALID_SCHEME_CHARS =
    		"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+.-";

    private static ServletMappingHelper servletMappingHelper =
    		new ServletMappingHelper(true);  
    
	private ServletUtils() {
	}

	/**
     * Returns <tt>true</tt> if our current URL is absolute,
     * <tt>false</tt> otherwise.
     */
    public static boolean isAbsoluteUrl(String url) {
		// a null URL is not absolute, by our definition
		if (url == null) {
		    return false;
		}
		// do a fast, simple check first
		int colonPos;
		if ((colonPos = url.indexOf(":")) == -1) {
		    return false;
		}
		// if we DO have a colon, make sure that every character
		// leading up to it is a valid scheme character
		for (int i = 0; i < colonPos; i++) {
		    if (VALID_SCHEME_CHARS.indexOf(url.charAt(i)) == -1) {
		    	return false;
		    }
		}
		// if so, we've got an absolute url
		return true;
    }
    
	public static String resolveUrl(String url,	HttpServletRequest request) {
		if (url == null || isAbsoluteUrl(url)) {
			return url;
		}
		if (url.startsWith("/")) {
			url = request.getContextPath() + url;
		}
		return url;
	}
		
	public static String resolveAndEncodeUrl(String url, 
			HttpServletRequest request, HttpServletResponse response) {
		
		if (url == null || isAbsoluteUrl(url)) {
			return url;
		}
		url = resolveUrl(url, request);
		return response.encodeURL(url);
	}
	
	public static String getOriginalRequestUri(HttpServletRequest request) {
		return servletMappingHelper.getRequestUri(request);
	}
	
	public static String getIncludeUri(HttpServletRequest request) {
		String uri = (String) request.getAttribute(
				INCLUDE_URI_REQUEST_ATTRIBUTE);
		
		if (uri == null) {
			uri = request.getRequestURI();
		}
		return uri;
	}
	
	public static Map takeAttributesSnapshot(HttpServletRequest request) {
		Map snapshot = new HashMap();
		Enumeration attrNames = request.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = (String) attrNames.nextElement();
			snapshot.put(attrName, request.getAttribute(attrName));
		}
		return snapshot;
	}
	
	/**
	 * Restores request attributes from the given map.
	 */
	public static void restoreAttributes(HttpServletRequest request, 
			Map attributesSnapshot) {
		
		// Copy into separate Collection to avoid side upon removal
		Set attrsToCheck = new HashSet();
		Enumeration attrNames = request.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = (String) attrNames.nextElement();
			attrsToCheck.add(attrName);
		}

		Iterator it = attrsToCheck.iterator(); 
		while (it.hasNext()) {
			String attrName = (String) it.next();
			Object attrValue = attributesSnapshot.get(attrName);
			if (attrValue != null) {
				request.setAttribute(attrName, attrValue);
			}
			else {
				request.removeAttribute(attrName);
			}
		}
	}
	
	/**
	 * Returns a map of request parameters. Unlike 
	 * {@link ServletRequest#getParameterMap()} this method returns Strings
	 * instead of String arrays. When more than one parameter with the same
	 * name is present, only the first value is put into the map.
	 */
	public static Map getSingularParameterMap(HttpServletRequest request) {
		HashMap params = new HashMap();
		Enumeration names = request.getParameterNames();
		while (names.hasMoreElements()) {
			String name = (String) names.nextElement();
			params.put(name, request.getParameter(name));
		}
		return params;
	}
	
	/**
	 * Returns the path of the given URI. Uses {@link java.net.URI}
	 * internally to parse the given String.
	 *  
	 * @throws IllegalArgumentException If the given string violates RFC 2396
	 */
	public static String getPath(String uri) {
		try {
			return new URI(uri).getPath(); 
		}
		catch (URISyntaxException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	/**
	 * Returns whether the <code>X-Requested-With</code> header is set to
	 * <code>XMLHttpRequest</code> as done by prototype.js.
	 */
	public static boolean isXmlHttpRequest(HttpServletRequest request) {
		return XML_HTTP_REQUEST.equals(request.getHeader(REQUESTED_WITH_HEADER));
	}

}
