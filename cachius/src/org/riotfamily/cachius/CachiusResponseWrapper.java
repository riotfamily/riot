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
package org.riotfamily.cachius;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.riotfamily.cachius.support.Cookies;
import org.riotfamily.cachius.support.Headers;
import org.riotfamily.cachius.support.SessionIdEncoder;
import org.riotfamily.common.web.util.DelegatingServletOutputStream;


/**
 * A HttpServletResponseWrapper that captures the response and updates
 * the associated CacheItem.
 *
 * @author Felix Gnass
 */
public class CachiusResponseWrapper extends HttpServletResponseWrapper {

    private CacheItem cacheItem;
    
    private SessionIdEncoder sessionIdEncoder;
    
    private ServletOutputStream outputStream;
    
    private PrintWriter writer;
    
    private int status = 0;
    
    private String contentType;
    
    private Headers headers = new Headers();
    
    private Cookies cookies = new Cookies();

	private boolean contentLengthSet;
    
        	
    public CachiusResponseWrapper(HttpServletResponse response, 
    		CacheItem cacheItem, SessionIdEncoder sessionIdEncoder) {
    	
        super(response);
        this.cacheItem = cacheItem;
        this.sessionIdEncoder = sessionIdEncoder;
    }
	    
    public void setStatus(int status) {
        super.setStatus(status);
        this.status = status;
    }
    
    public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContentLength(int len) {
		contentLengthSet = true;
    }
    
    public void addDateHeader(String name, long date) {
    	headers.addDate(name, date);
    }
    
    public void setDateHeader(String name, long date) {
    	headers.setDate(name, date);
    }
    
    public void addIntHeader(String name, int value) {
    	headers.addInt(name, value);
    }
    
    public void setIntHeader(String name, int value) {
    	headers.setInt(name, value);
    }
    
    public void addHeader(String name, String value) {
        headers.add(name, value);
    }

    public void setHeader(String name, String value) {
        headers.set(name, value);
    }

    public void addCookie(Cookie cookie) {
        cookies.add(cookie);
    }    
    
    /**
     * Returns an ServletOutputStream that writes into the OutputStream
     * provided by the CacheItem. All output is redirected so nothing will be 
     * sent to the client.
     *
     * @throws IllegalStateException If getWriter() has been called before
     * @throws IOException
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException();
        }
        if (outputStream == null) {
        		outputStream = new DelegatingServletOutputStream(
        				cacheItem.getOutputStream());
        }
        return outputStream;
    }

    /**
     * Returns a PrintWriter that writes into the Writer provided by the 
     * CacheItem. All output is redirected so nothing will be 
     * sent to the client.
     *
     * @throws IllegalStateExcepion If getOutputStream() has been called before
     * @throws IOException
     */
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            if (outputStream != null) {
                throw new IllegalStateException();
            }
	        writer = new PrintWriter(cacheItem.getWriter(
	        		sessionIdEncoder.getSessionId()));
	    }
        return writer;
    }
    
    public void flushBuffer() throws IOException {
    	if (writer != null) {
    		writer.flush();
    	}
    	else if (outputStream != null) {
    		outputStream.flush();
    	}
    }
    
    public void stopCapturing(long lastModified) throws IOException {
    	flushBuffer();
    	if (writer != null) {
    		writer.close();
    	}
    	else if (outputStream != null) {
    		outputStream.close();
    	}
    	if (status == 0 || status == HttpServletResponse.SC_OK) {
        	cacheItem.setLastModified(lastModified);
        }
    	else {
    		cacheItem.invalidate();
    	}
    }
    
    /**
     * Sets the captured headers on the CacheItem.
     */
    public void updateHeaders() {
    	cacheItem.setContentType(contentType);
    	cacheItem.setHeaders(headers);
    	cacheItem.setCookies(cookies);
    	cacheItem.setSetContentLength(contentLengthSet);
    }
    
    /**
     * Delegates the call to {@link SessionIdEncoder#encodeRedirectURL(String)}
     * to ensure that the session state remains the same during processing.
     */
    public String encodeRedirectURL(String url) {
        return sessionIdEncoder.encodeRedirectURL(url);
    }

    /**
     * Delegates the call to {@link #encodeRedirectURL(String)}.
     */
    public String encodeRedirectUrl(String url) {
        return encodeRedirectURL(url);
    }

    /**
     * Delegates the call to {@link SessionIdEncoder#encodeURL(String)}
     * to ensure that the session state remains the same during processing.
     */
    public String encodeURL(String url) {
        return sessionIdEncoder.encodeURL(url);
    }

    /**
     * Delegates the call to {@link #encodeURL(String)}.
     */
    public String encodeUrl(String url) {
        return encodeURL(url);
    }

    
}
