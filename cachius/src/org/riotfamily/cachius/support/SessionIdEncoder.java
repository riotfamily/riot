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
package org.riotfamily.cachius.support;

import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utility class for working with URL-based session tracking.
 */
public class SessionIdEncoder {

	private String sessionId;
	
	private boolean requestedSessionIdFromCookie;
	
	private String scheme;

	private String serverName;

	private int serverPort;

	private String contextPath;

	private String requestUrl;
	
	public SessionIdEncoder(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (session != null) {
			sessionId = session.getId();
			requestedSessionIdFromCookie = request.isRequestedSessionIdFromCookie();
		}
		scheme = request.getScheme();
		serverName = request.getServerName();
		serverPort = request.getServerPort();
		contextPath = request.getContextPath();
		requestUrl = request.getRequestURL().toString();
	}
	
	public String getSessionId() {
		return this.sessionId;
	}
	
	/**
	 * 
	 */
	public boolean urlsNeedEncoding() {
		if (sessionId == null) {
            return false;
        }
        if (requestedSessionIdFromCookie) {
            return false;
        }
        return true;
	}
	
	/**
     * Encode the session identifier associated with this response
     * into the specified URL, if necessary.
     *
     * @param url URL to be encoded
     */
    public String encodeURL(String url) {
        String absolute = toAbsolute(url);
        if (isEncodeable(absolute)) {
            // W3c spec clearly said 
            if (url.equalsIgnoreCase("")) {
                url = absolute;
            }
            return toEncoded(url);
        } 
        else {
            return url;
        }
    }
    
    /**
     * Encode the session identifier associated with this response
     * into the specified redirect URL, if necessary.
     *
     * @param url URL to be encoded
     */
    public String encodeRedirectURL(String url) {
        if (isEncodeable(toAbsolute(url))) {
            return toEncoded(url);
        } 
        else {
            return url;
        }
    }
    
    /**
     * Return <code>true</code> if the specified URL should be encoded with
     * a session identifier.  This will be true if all of the following
     * conditions are met:
     * <ul>
     * <li>The request we are responding to asked for a valid session
     * <li>The requested session ID was not received via a cookie
     * <li>The specified URL points back to somewhere within the web
     *     application that is responding to this request
     * </ul>
     *
     * @param location Absolute URL to be validated
     */
    private boolean isEncodeable(String location) {
        if (location == null) {
            return false;
        }
        
        // Are we in a valid session that is not using cookies?
        if (!urlsNeedEncoding()) {
        	return false;
        }
        
        // Is this an intra-document reference?
        if (location.startsWith("#")) {
            return false;
        }

        // Is this a valid absolute URL?
        URL url = null;
        try {
            url = new URL(location);
        }
        catch (MalformedURLException e) {
            return false;
        }

        // Does this URL match down to (and including) the context path?
        if (!scheme.equalsIgnoreCase(url.getProtocol())) {
            return false;
        }
        if (!serverName.equalsIgnoreCase(url.getHost())) {
            return false;
        }
        if (serverPort == -1) {
            if ("https".equals(scheme)) {
                serverPort = 443;
            }
            else {
                serverPort = 80;
            }
        }
        int urlPort = url.getPort();
        if (urlPort == -1) {
            if ("https".equals(url.getProtocol())) {
                urlPort = 443;
            }
            else {
                urlPort = 80;
            }
        }
        if (serverPort != urlPort) {
            return false;
        }

        if (contextPath != null) {
            String file = url.getFile();
            if (file == null || !file.startsWith(contextPath)) {
                return false;
            }
            if (file.indexOf(";jsessionid=" + sessionId) >= 0) {
                return false;
            }
        }

        // This URL belongs to our web application, so it is encodeable
        return (true);
    }


    /**
     * Convert (if necessary) and return the absolute URL that represents the
     * resource referenced by this possibly relative URL.  If this URL is
     * already absolute, return it unchanged.
     *
     * @param location URL to be (possibly) converted and then returned
     *
     * @exception IllegalArgumentException if a MalformedURLException is
     *  thrown when converting the relative URL to an absolute one
     */
    private String toAbsolute(String location) {
        if (location == null) {
            return location;
        }
        // Construct a new absolute URL if possible
        URL url = null;
        try {
            url = new URL(location);
            if (url.getAuthority() == null) {
                return location;
            }
        } 
        catch (MalformedURLException e1) {
            try {
                url = new URL(new URL(requestUrl), location);
            } 
            catch (MalformedURLException e2) {
                throw new IllegalArgumentException(location);
            }
        }
        return url.toExternalForm();
    }


    /**
     * Return the specified URL with the session identifier suitably encoded.
     * 
     * @param url URL to be encoded with the session id
     */
    private String toEncoded(String url) {
        if (url == null || sessionId == null) {
            return url;
        }
        String path = url;
        String query = "";
        String anchor = "";
        int question = url.indexOf('?');
        if (question >= 0) {
            path = url.substring(0, question);
            query = url.substring(question);
        }
        int pound = path.indexOf('#');
        if (pound >= 0) {
            anchor = path.substring(pound);
            path = path.substring(0, pound);
        }
        StringBuffer sb = new StringBuffer(path);
        if( sb.length() > 0 ) { // jsessionid can't be first.
            sb.append(";jsessionid=");
            sb.append(sessionId);
        }
        sb.append(anchor);
        sb.append(query);
        return sb.toString();
    }

    public Writer createIdRemovingWriter(Writer out) {
    	return new TokenFilterWriter(getSessionId(), "${jsessionid}", out);
    }
    
    public Writer createIdInsertingWriter(Writer out) {
    	return new TokenFilterWriter("${jsessionid}", getSessionId(), out);
    }
}
