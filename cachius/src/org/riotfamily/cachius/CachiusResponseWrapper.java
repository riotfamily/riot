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
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.riotfamily.cachius.support.SessionIdEncoder;
import org.riotfamily.cachius.support.TokenFilterWriter;
import org.riotfamily.common.web.util.DelegatingServletOutputStream;


/**
 * A HttpServletResponseWrapper that captures the response and updates
 * the associated CacheItem in case no error occurs during request processing.
 *
 * @author Felix Gnass
 */
public class CachiusResponseWrapper extends HttpServletResponseWrapper {

	private static Log log = LogFactory.getLog(CachiusResponseWrapper.class);
	
	private static final String HEADER_EXPIRES = "Expires";
	
	private static final String HEADER_CACHE_CONTROL = "Cache-Control";
	
    private CacheItem cacheItem;
    
    private SessionIdEncoder sessionIdEncoder;
    
    private ServletOutputStream outputStream;
    
    private PrintWriter writer;
        
 
    public CachiusResponseWrapper(HttpServletResponse response, 
    		CacheItem cacheItem, SessionIdEncoder sessionIdEncoder) {
    	
        super(response);
        this.cacheItem = cacheItem;
        this.sessionIdEncoder = sessionIdEncoder;
    }
	    
    /**
     * Set the HTTP status code
     *
     * @param sc The status
     */
    public void setStatus(int sc) {
        super.setStatus(sc);
        if (sc != 0 && sc != HttpServletResponse.SC_OK) {
        	cacheItem.setLastModified(-1);
        }
    }
       
    
    public void setContentType(String contentType) {
        super.setContentType(contentType);
        cacheItem.setContentType(contentType);
    }
    
    public void setDateHeader(String name, long date) {
       	super.setDateHeader(name, date);
       	checkExpires(name, date);
    }
    
    public void addDateHeader(String name, long date) {
    	super.addDateHeader(name, date);
    	checkExpires(name, date);
    }
    
    private void checkExpires(String name, long date) {
    	if (HEADER_EXPIRES.equalsIgnoreCase(name)) {
    		cacheItem.setExpires(date);
    	}
    }
    
    public void setHeader(String name, String value) {
    	super.setHeader(name, value);
    	checkCacheControl(name, value);
    }
    
    public void addHeader(String name, String value) {
    	super.addHeader(name, value);
    	checkCacheControl(name, value);
    }
    
    private void checkCacheControl(String name, String value) {
    	if (HEADER_CACHE_CONTROL.equalsIgnoreCase(name)) {
    		cacheItem.setCacheControl(value);
    	}
    }
    
    /**
     * Get an OutputStream
     *
     * @throws IllegalStateException If getWriter() has been called before
     * @throws IOException
     */
    public ServletOutputStream getOutputStream() throws IOException {
        if (writer != null) {
            throw new IllegalStateException();
        }
        if (outputStream == null) {
        	try {
        		outputStream = new DelegatingServletOutputStream( 
        				cacheItem.getOutputStream());
        	}
        	catch (IOException e) {
        		log.warn(e);
        		// Fail gracefully - continue without caching
        		outputStream = super.getOutputStream();
        	}
        }
        return outputStream;
    }

    /**
     * Get a PrintWriter
     *
     * @throws IllegalStateExcepion If getOutputStream() has been called before
     * @throws IOException
     */
    public PrintWriter getWriter() throws IOException {
        if (writer == null) {
            if (outputStream != null) {
                throw new IllegalStateException();
            }
            try {
            	Writer itemWriter = cacheItem.getWriter();
		        if (cacheItem.isFilterSessionId()) {
		        	itemWriter = new TokenFilterWriter(
		        			sessionIdEncoder.getSessionId(),
		                    "${jsessionid}", itemWriter);
		        }
		        writer = new PrintWriter(itemWriter);
		        
            }
            catch (IOException e) {
            	log.warn(e);
            	// Fail gracefully - continue without caching
            	writer = super.getWriter(); 
            }
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
    
    public void stopCapturing() throws IOException {
    	flushBuffer();
    	if (writer != null) {
    		writer.close();
    	}
    	else if (outputStream != null) {
    		outputStream.close();
    	}
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
