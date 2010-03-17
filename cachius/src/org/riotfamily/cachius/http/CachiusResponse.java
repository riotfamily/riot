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
package org.riotfamily.cachius.http;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.http.content.BinaryContent;
import org.riotfamily.cachius.http.content.CharacterContent;
import org.riotfamily.cachius.http.content.ChunkedContent;
import org.riotfamily.cachius.http.content.ContentFragment;
import org.riotfamily.cachius.http.content.Directives;
import org.riotfamily.cachius.http.content.GzipContent;
import org.riotfamily.cachius.http.header.SessionIdCookie;
import org.riotfamily.cachius.http.header.StaticCookie;
import org.riotfamily.cachius.http.support.DelegatingServletOutputStream;
import org.riotfamily.cachius.http.support.ScanWriter;
import org.riotfamily.cachius.http.support.SessionIdEncoder;
import org.riotfamily.cachius.http.support.ScanWriter.Block;
import org.riotfamily.cachius.persistence.DiskStore;


/**
 * Implementation of the HttpServletResponse interface that captures the 
 * response in the given {@link ResponseData} object.
 *
 * @author Felix Gnass
 */
public class CachiusResponse implements HttpServletResponse {

    private ResponseData data;
    
    private SessionIdEncoder sessionIdEncoder;
    
    private boolean compressible;
    
    private long gzipThreshold;
    
    private Directives directives;
    
    private DiskStore diskStore;
    
    private ServletOutputStream outputStream;
    
    private PrintWriter writer;
    
    private ScanWriter scanWriter;
    
    private File file;

    public CachiusResponse(ResponseData data, DiskStore diskStore, 
    		SessionIdEncoder sessionIdEncoder, boolean compressible,
    		int gzipThreshold, Directives directives) throws IOException {
    	
    	this.data = data;
    	this.diskStore = diskStore;
    	this.sessionIdEncoder = sessionIdEncoder;
    	this.compressible = compressible;
    	this.gzipThreshold = gzipThreshold;
        this.directives = directives;
        this.file = diskStore.getFile();
    }
	
    public int getStatus() {
		return data.getStatus();
	}
    
    public void setStatus(int status) {
        data.setStatus(status);
    }
    
    public void setStatus(int status, String msg) {
    	data.setStatus(status);
	}
    
    public void sendError(int status) throws IOException {
    	data.setError(status, null);
    }
    
    public void sendError(int status, String msg) throws IOException {
    	data.setError(status, msg);
    }
    
    public void sendRedirect(String location) throws IOException {
    	data.getHeaders().add("Location", location);
    	setStatus(302);
    }
    
    public String getContentType() {
		return data.getContentType();
	}

	public void setContentType(String contentType) {
		if (writer == null) {
			data.setContentType(contentType);
		}
	}

	public void setContentLength(int len) {
    }
    
	public boolean containsHeader(String name) {
		return data.getHeaders().contain(name);
	}

    public void addDateHeader(String name, long date) {
    	data.getHeaders().addDate(name, date);
    }
    
    public void setDateHeader(String name, long date) {
    	data.getHeaders().setDate(name, date);
    }
    
    public void addIntHeader(String name, int value) {
    	data.getHeaders().addInt(name, value);
    }
    
    public void setIntHeader(String name, int value) {
    	data.getHeaders().setInt(name, value);
    }
    
    public void addHeader(String name, String value) {
    	data.getHeaders().add(name, value);
    }

    public void setHeader(String name, String value) {
    	data.getHeaders().set(name, value);
    }

    public void addCookie(Cookie cookie) {
    	if (sessionIdEncoder.isSessionIdCookie(cookie)) {
    		data.getCookies().add(new SessionIdCookie(cookie));
    	}
    	else {
    		data.getCookies().add(new StaticCookie(cookie));
    	}
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
        				new BufferedOutputStream(new FileOutputStream(file)));
        }
        return outputStream;
    }

    /**
     * Returns a PrintWriter that writes into the Writer provided by the 
     * {@link ResponseData}. All output is redirected so nothing will be 
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
            OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
            scanWriter = directives.createWriter(new OutputStreamWriter(out, "UTF-8"));
	        writer = new PrintWriter(scanWriter);
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
    	if (isChunked()) {
    		ChunkedContent content = new ChunkedContent(file);
    		for (Block block : scanWriter.getBlocks()) {
    			ContentFragment fragment = directives.parse(block.getValue());
    			if (fragment != null) {
    				content.addFragment(block.getStart(), block.getEnd(), fragment);
    			}
    		}
    		content.addTail();
    		data.setContent(content);
    	}
    	else if (isGzip()) {
			data.setContent(new GzipContent(file, diskStore.getFile()));
    	}
    	else if (isCharacter()) {
    		data.setContent(new CharacterContent(file));
    	}	
    	else {
    		data.setContent(new BinaryContent(file));
    	}
    }
    
    private boolean isCharacter() {
		return scanWriter != null;
	}
    
	private boolean isChunked() {
		return isCharacter() && scanWriter.foundBlocks();
	}
	
	private boolean isGzip() {
		return compressible && file.length() > gzipThreshold;
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
		data.setCharacterEncoding(characterEncoding);
	}

	public String getCharacterEncoding() {
		return data.getCharacterEncoding();
	}
	
	public void setLocale(Locale locale) {
		if (locale == null || writer != null) {
            return;
        }
        data.setLocale(locale);
        String language = locale.getLanguage();
        if ((language != null) && (language.length() > 0)) {
            String country = locale.getCountry();
            if (country != null && country.length() > 0) {
                language += '-' + country;
            }
            setHeader("Content-Language", language);
        }
	}
	
	public Locale getLocale() {
		return data.getLocale();
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
		data.clear();
	}

	public void resetBuffer() {
		try {
			if (writer != null) {
	    		writer.close();
	    	}
	    	else if (outputStream != null) {
	    		outputStream.close();
	    	}
		}
		catch (IOException e) {
		}
		writer = null;
		outputStream = null;
	}

}
