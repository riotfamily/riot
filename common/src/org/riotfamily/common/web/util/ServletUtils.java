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
package org.riotfamily.common.web.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.xml.DocumentReader;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.springframework.web.context.support.ServletContextResource;
import org.springframework.web.util.UrlPathHelper;
import org.springframework.web.util.WebUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public final class ServletUtils {

	private static final long FAR_FUTURE = 307584000000L;

	public static final String INCLUDE_URI_REQUEST_ATTRIBUTE =
			"javax.servlet.include.request_uri";

	private static final String PRAGMA_HEADER = "Pragma";

	private static final String EXPIRES_HEADER = "Expires";

	private static final String CACHE_CONTROL_HEADER = "Cache-Control";

	public static final String REQUESTED_WITH_HEADER = "X-Requested-With";

	public static final String XML_HTTP_REQUEST = "XMLHttpRequest";

	public static final String SCHEME_HTTP = "http";

	public static final String SCHEME_HTTPS = "https";

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

    private static UrlPathHelper urlPathHelper = new UrlPathHelper();

	private ServletUtils() {
	}

	/**
	 * Return the context path for the given request, detecting an include request
	 * URL if called within a RequestDispatcher include.
	 * @see UrlPathHelper#getOriginatingContextPath(HttpServletRequest)
	 */
	public static String getOriginatingContextPath(HttpServletRequest request) {
		return urlPathHelper.getOriginatingContextPath(request);
	}
	
	/**
	 * Return the request URI for the given request, detecting an include request
	 * URL if called within a RequestDispatcher include.
	 * @see UrlPathHelper#getRequestUri(HttpServletRequest)
	 */
	public static String getRequestUri(HttpServletRequest request) {
		return urlPathHelper.getRequestUri(request);
	}
	
	/**
	 * Return the request URI for root of the given request. If this is a 
	 * forwarded request, correctly resolves to the request URI of the original 
	 * request.
	 * @see UrlPathHelper#getOriginatingRequestUri(HttpServletRequest)
	 */
	public static String getOriginatingRequestUri(HttpServletRequest request) {
		return urlPathHelper.getOriginatingRequestUri(request);
	}

	public static String getOriginatingServletPath(HttpServletRequest request) {
		String servletPath = (String) request.getAttribute(
				WebUtils.FORWARD_SERVLET_PATH_ATTRIBUTE);

		if (servletPath == null) {
			servletPath = request.getServletPath();
		}
		return servletPath;
	}

	/**
	 * Return the path within the web application for the given request.
	 * @see UrlPathHelper#getPathWithinApplication(HttpServletRequest)
	 */
	public static String getPathWithinApplication(HttpServletRequest request) {
		return urlPathHelper.getPathWithinApplication(request);
	}

	/**
	 * Return the path within the web application for the given request.
	 * @param request current HTTP request
	 * @return the path within the web application
	 */
	public static String getOriginatingPathWithinApplication(HttpServletRequest request) {
		String contextPath = getOriginatingContextPath(request);
		String requestUri = getOriginatingRequestUri(request);
		if (StringUtils.startsWithIgnoreCase(requestUri, contextPath)) {
			// Normal case: URI contains context path.
			String path = requestUri.substring(contextPath.length());
			return (StringUtils.hasText(path) ? path : "/");
		}
		else {
			// Special case: rather unusual.
			return requestUri;
		}
	}

	/**
	 * Return the path within the servlet mapping for the given request,
	 * i.e. the part of the request's URL beyond the part that called the servlet,
	 * or "" if the whole URL has been used to identify the servlet.
	 * <p>E.g.: servlet mapping = "/test/*"; request URI = "/test/a" -> "/a".
	 * <p>E.g.: servlet mapping = "/test"; request URI = "/test" -> "".
	 * <p>E.g.: servlet mapping = "/*.test"; request URI = "/a.test" -> "".
	 * @param request current HTTP request
	 * @return the path within the servlet mapping, or ""
	 */
	public static String getOriginatingPathWithinServletMapping(HttpServletRequest request) {
		String pathWithinApp = getOriginatingPathWithinApplication(request);
		String servletPath = getOriginatingServletPath(request);
		if (pathWithinApp.startsWith(servletPath)) {
			// Normal case: URI contains servlet path.
			return pathWithinApp.substring(servletPath.length());
		}
		else {
			// Special case: URI is different from servlet path.
			// Can happen e.g. with index page: URI="/", servletPath="/index.html"
			// Use servlet path in this case, as it indicates the actual target path.
			return servletPath;
		}
	}

	/**
	 * Returns the lookup-path for a given request. This is either the path
	 * within the servlet-mapping (in case of a prefix mapping) or the path
	 * within the application without the trailing suffix (in case of a suffix
	 * mapping).
	 */
	public static String getPathWithoutServletMapping(
			HttpServletRequest request) {

		String path = urlPathHelper.getPathWithinServletMapping(request);
		if (path.length() == 0) {
			path = urlPathHelper.getPathWithinApplication(request);
			if (path.equals(getServletPrefix(request))) {
				return "/";
			}
			int dotIndex = path.lastIndexOf('.');
			if (dotIndex != -1 && dotIndex > path.lastIndexOf('/')) {
				path = path.substring(0, dotIndex);
			}
		}
		return path;
	}

	/**
	 * Returns the lookup-path for a given request. This is either the path
	 * within the servlet-mapping (in case of a prefix mapping) or the path
	 * within the application without the trailing suffix (in case of a suffix
	 * mapping).
	 */
	public static String getOriginatingPathWithoutServletMapping(
			HttpServletRequest request) {

		String path = getOriginatingPathWithinServletMapping(request);
		if (path.length() == 0) {
			path = getOriginatingPathWithinApplication(request);
			if (path.equals(getServletPrefix(request))) {
				return "/";
			}
			int dotIndex = path.lastIndexOf('.');
			if (dotIndex >= 0 && dotIndex > path.lastIndexOf('/')) {
				path = path.substring(0, dotIndex);
			}
		}
		return path;
	}

	/**
	 * Returns the servlet-mapping prefix for the given request or an empty
	 * String if the servlet is mapped by a suffix.
	 */
	public static String getServletPrefix(HttpServletRequest request) {
		String path = urlPathHelper.getPathWithinApplication(request);
		String servletPath = urlPathHelper.getServletPath(request);
		if (path.length() > servletPath.length()
				|| (path.equals(servletPath) && path.indexOf('.') == -1)) {
			
			return servletPath;
		}
		return "";
	}

	/**
	 * Returns the servlet-mapping suffix for the given request or an empty
	 * String if the servlet is mapped by a prefix.
	 */
	public static String getServletSuffix(HttpServletRequest request) {
		String path = urlPathHelper.getPathWithinApplication(request);
		if (path.equals(urlPathHelper.getServletPath(request))) {
			int dotIndex = path.lastIndexOf('.');
			if (dotIndex >= 0 && dotIndex > path.lastIndexOf('/')) {
				return path.substring(dotIndex);
			}
		}
		return "";
	}

	/**
	 * Adds the mapping of the servlet that is mapped to the given request
	 * to the path.
	 */
	public static String addServletMapping(String path, 
			HttpServletRequest request) {
		
		String suffix = getServletPrefix(request);
		if (suffix.length() > 0) {
			return path + suffix;
		}
		return getServletPrefix(request) + path;
	}

	/**
	 * Returns a String consisting of the context-path and the servlet-prefix
	 * for the given request. The String will always end with a slash.
	 */
	public static String getRootPath(HttpServletRequest request) {
		StringBuffer path = new StringBuffer();
		path.append(getOriginatingContextPath(request));
		path.append(getServletPrefix(request));
		path.append('/');
		return path.toString();
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

    /**
	 * @since 6.4
     */
    public static boolean isHttpUrl(String url) {
    	return isAbsoluteUrl(url) && url.startsWith("http");
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
	 * Returns the host of the given URI. Uses {@link java.net.URI}
	 * internally to parse the given String. If the given string violates 
	 * RFC 2396 <code>null</code> will be returned.
	 */
	public static String getHost(String uri) {
		try {
			return new URI(uri).getHost();
		}
		catch (URISyntaxException e) {
			return null;
		}
	}
	
	/**
	 * Returns the path of the given URI. Uses {@link java.net.URI}
	 * internally to parse the given String.  If the given string violates 
	 * RFC 2396 <code>null</code> will be returned.
	 */
	public static String getPath(String uri) {
		try {
			return new URI(uri).getPath();
		}
		catch (URISyntaxException e) {
			return null;
		}
	}

	/**
	 * Returns a StringBuffer containing an URL with the protocol, hostname
	 * and port (unless it's the protocol's default port) of the given request.
	 */
	public static StringBuffer getAbsoluteUrlPrefix(HttpServletRequest request) {
		StringBuffer url = new StringBuffer();
        url.append(request.getScheme());
        url.append("://");
        url.append(getServerNameAndPort(request));
        return url;
	}

	/**
	 * Returns the serverName and port (if applicable) for the given request. 
	 */
	public static String getServerNameAndPort(HttpServletRequest request) {
		if (request.getServerPort() != 0 
				&& request.getServerPort() != 80
				&& request.getServerPort() != 443) {
			
			return request.getServerName() + ":" + request.getServerPort();
		}
		return request.getServerName();
	}
	
	/**
	 * Returns whether the <code>X-Requested-With</code> header is set to
	 * <code>XMLHttpRequest</code> as done by prototype.js.
	 */
	public static boolean isXmlHttpRequest(HttpServletRequest request) {
		return XML_HTTP_REQUEST.equals(request.getHeader(REQUESTED_WITH_HEADER));
	}

	/**
	 * Returns whether the given request is a direct request, i.e. not 
	 * dispatched by a RequestDispatcher.
	 */
	public static boolean isDirectRequest(HttpServletRequest request) {
		return request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE) == null 
				&& request.getAttribute(WebUtils.FORWARD_REQUEST_URI_ATTRIBUTE) == null;
	}

	/**
	 * Sets Pragma, Expires and Cache-Control headers to prevent caching.
	 * @since 6.4
	 */
	public static void setNoCacheHeaders(HttpServletResponse response) {
		response.setHeader(PRAGMA_HEADER, "No-cache");
		response.setDateHeader(EXPIRES_HEADER, 1L);
		response.setHeader(CACHE_CONTROL_HEADER, "no-cache");
		response.addHeader(CACHE_CONTROL_HEADER, "no-store");
	}

	/**
	 * Sets Expires and Cache-Control headers to allow caching for the given
	 * period.
	 * @see FormatUtils#parseMillis(String)
	 * @since 6.5
	 */
	public static void setCacheHeaders(HttpServletResponse response, String period) {
		long millis = FormatUtils.parseMillis(period);
		response.setDateHeader(EXPIRES_HEADER, System.currentTimeMillis() + millis);
		response.setHeader(CACHE_CONTROL_HEADER, "max-age=" + millis / 1000L);
	}
	
	/**
	 * Sets an far future Expires header.
	 */
	public static void setFarFutureExpiresHeader(HttpServletResponse response) {
		response.setDateHeader(EXPIRES_HEADER, System.currentTimeMillis() + 
				FAR_FUTURE);
	}

	/**
	 * Parses the web.xml deployment descriptor and returns the url-pattern
	 * for the given servlet-name, or <code>null</code> if no mapping is found.
	 * @since 6.4
	 */
	public static String getServletMapping(String servletName,
			ServletContext servletContext) {

		DocumentReader reader = new DocumentReader(new ServletContextResource(
				servletContext,	"/WEB-INF/web.xml"));

		Document doc = reader.readDocument();
		Iterator it = DomUtils.getChildElementsByTagName(
				doc.getDocumentElement(), "servlet-mapping").iterator();

		while (it.hasNext()) {
			Element e = (Element) it.next();
			Element name = DomUtils.getChildElementByTagName(e, "servlet-name");
			if (servletName.equals(DomUtils.getTextValue(name))) {
				return DomUtils.getTextValue(DomUtils.getChildElementByTagName(
						e, "url-pattern")).trim();
			}
		}
		return null;
	}

	/**
	 * This method tries to replace the given parameter's value in the given URL's
	 * query string with the given new value or adds the parameter if it is not
	 * yet contained. The modified URL then is returned. 
	 */
	public static String setParameter(String url, String name, String value) {
		Pattern pattern = Pattern.compile("([?&]" + name + "=)(.*)?(&|$)");
		Matcher m = pattern.matcher(url);
		if (m.find()) {
			return m.replaceFirst("$1" + FormatUtils.uriEscape(value) + "$3");
		}
		else {
			return addParameter(url, name, value);
		}
	}
	
	/**
	 * Returns an URL with the given parameter added to the given URL's query string.
	 */
	public static String addParameter(String url, String name, String value) {
		StringBuffer sb = new StringBuffer(url);
		boolean first = url.indexOf('?') == -1;
		sb.append(first ? '?' : '&');
		sb.append(name);
		if (value != null) {
			sb.append('=').append(FormatUtils.uriEscape(value));
		}
		return sb.toString();
	}

	/**
	 * Returns an URL with all of the given request's parameters added to the
	 * given URL's query string.
	 */
	public static String addRequestParameters(String url, HttpServletRequest request) {
		Enumeration e = request.getParameterNames();
		while (e.hasMoreElements()) {
			String name = (String) e.nextElement();
			String[] values = request.getParameterValues(name);
			for (int i=0; i < values.length; i++) {
				url = addParameter(url, name, values[i]);
			}
		}
		return url;
	}
	

	public static String getRequestUrlWithQueryString(
			HttpServletRequest request) {

		request.getRequestURI();
		StringBuffer sb = request.getRequestURL();
		String queryString = request.getQueryString();
		if (queryString != null) {
			sb.append('?').append(queryString);
		}
		return sb.toString();
	}

}
