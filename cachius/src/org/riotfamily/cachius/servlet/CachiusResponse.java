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
package org.riotfamily.cachius.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.CacheItem;
import org.riotfamily.cachius.support.Cookies;
import org.riotfamily.cachius.support.Headers;
import org.riotfamily.cachius.support.SessionIdEncoder;
import org.riotfamily.common.servlet.DelegatingServletOutputStream;


/**
 * Implementation of the HttpServletResponse interface that captures the 
 * response and updates the associated CacheItem.
 *
 * @author Felix Gnass
 */
public class CachiusResponse implements HttpServletResponse {

    private CacheItem cacheItem;
    
    private SessionIdEncoder sessionIdEncoder;
    
    private ServletOutputStream outputStream;
    
    private PrintWriter writer;
    
    private int status = 0;
    
    private String contentType;
    
    private Headers headers = new Headers();
    
    private Cookies cookies = new Cookies();

	private String characterEncoding = "UTF-8";

	private Locale locale;
    
        	
    public CachiusResponse(CacheItem cacheItem, 
    		SessionIdEncoder sessionIdEncoder, String characterEncoding) {
    	
        this.cacheItem = cacheItem;
        this.sessionIdEncoder = sessionIdEncoder;
        this.characterEncoding = characterEncoding;
    }
	
    public int getStatus() {
		return status;
	}
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public void setStatus(int status, String msg) {
    	this.status = status;
	}
    
    public void sendError(int status) throws IOException {
    	this.status = status;
    }
    
    public void sendError(int status, String msg) throws IOException {
    	this.status = status;
    }
    
    public void sendRedirect(String location) throws IOException {
    	headers.add("Location", location);
    	status = 302;
    }
    
    public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setContentLength(int len) {
    }
    
	public boolean containsHeader(String name) {
		return headers.contain(name);
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
            Writer itemWriter = cacheItem.getWriter();
            if (sessionIdEncoder.urlsNeedEncoding()) {
               	itemWriter = sessionIdEncoder.createIdRemovingWriter(itemWriter);
            }
	        writer = new PrintWriter(itemWriter);
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
    	resetBuffer();
    }
    
    /**
     * Sets the captured headers on the CacheItem.
     */
    public void updateHeaders() {
    	cacheItem.setStatus(status);
    	cacheItem.setContentType(contentType);
    	cacheItem.setHeaders(headers);
    	cacheItem.setCookies(cookies);
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

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}
	
	public void setLocale(Locale locale) {
		if (locale == null) {
            return;
        }
        this.locale = locale;
        String language = locale.getLanguage();
        if ((language != null) && (language.length() > 0)) {
            String country = locale.getCountry();
            StringBuilder sb = new StringBuilder(language);
            if (country != null && country.length() > 0) {
                sb.append('-');
                sb.append(country);
            }
            language = sb.toString();
        }
        setHeader("Content-Language", language);
	}
	
	public Locale getLocale() {
		return locale;
	}

	public boolean isCommitted() {
		return false;
	}

	public int getBufferSize() {
		return 0;
	}
	
	public void setBufferSize(int size) {
	}

	public void reset() {
		resetBuffer();
		headers.clear();
		cookies.clear();
		status = 0;
		contentType = null;
		characterEncoding = null;
		locale = null;
	}

	public void resetBuffer() {
		try {
			if (writer != null) {
	    		writer.close();
	    	}
	    	else if (outputStream != null) {
	    		outputStream.close();
	    	}
	    	else {
	        	// If never getWriter() nor getOutputStream() have been called, the
	        	// content is empty and the cacheItem will be emptied.
	    		cacheItem.clear();
	    	}
		}
		catch (IOException e) {
		}
		writer = null;
		outputStream = null;
	}

}
