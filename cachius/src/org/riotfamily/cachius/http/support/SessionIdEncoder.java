/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.cachius.http.support;

import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Utility class for working with URL-based session tracking.
 */
public class SessionIdEncoder {

	private String scheme;

	private String serverName;

	private int serverPort;

	private String contextPath;

	private String requestUrl;
	
	private String sessionId;
	
	public SessionIdEncoder(HttpServletRequest request) {
		scheme = request.getScheme();
		serverName = request.getServerName();
		serverPort = request.getServerPort();
		contextPath = request.getContextPath();
		requestUrl = request.getRequestURL().toString();
		if (!request.isRequestedSessionIdFromCookie()) {
			HttpSession session = request.getSession(false);
			sessionId = session != null ? session.getId() : null;
		}
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
            if (containsSessionId(file)) {
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
        if (url == null) {
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
        StringBuilder sb = new StringBuilder(path);
        if( sb.length() > 0) {
            appendSessionId(sb);
        }
        sb.append(anchor);
        sb.append(query);
        return sb.toString();
    }
    
    public boolean containsSessionId(String file) {
    	return file.indexOf("(@sessionid)") >= 0;
	}

    public void appendSessionId(StringBuilder sb) {
    	sb.append("(@sessionid)");
    }
    
    public String replaceSessionId(String url) {
    	String replacement = "";
    	if (sessionId != null) {
    		replacement = ";jsessionid=" + sessionId; 
    	}
    	return url.replace("(@sessionid)", replacement);
    }

	public boolean isSessionIdCookie(Cookie cookie) {
		return sessionId != null && sessionId.equals(cookie.getValue());
	}

}
