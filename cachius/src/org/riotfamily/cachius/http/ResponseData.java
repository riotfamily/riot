package org.riotfamily.cachius.http;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.cachius.http.content.Content;
import org.riotfamily.cachius.http.support.Cookies;
import org.riotfamily.cachius.http.support.Headers;


public class ResponseData implements Serializable {

	/** The HTTP Status code */
    private int statusCode;
    
    private boolean error;
    
    private String errorMessage;
    
    /** The Content-Type of the cached data */
    private String contentType;
    
    /** Captured HTTP headers that will be sent */
    private Headers headers;
    
    /** Captured cookies that will be sent */
    private Cookies cookies;
    
    private String characterEncoding;

	private Locale locale;
	
	private Content content;
	 
    public ResponseData(String characterEncoding) {
    	this.characterEncoding = characterEncoding;
    }

    public void clear() {
    	error = false;
		statusCode = 0;
		errorMessage = null;
		contentType = null;
		headers.clear();
    	cookies.clear();
    }
    
	public int getStatus() {
		return statusCode;
	}

	public void setStatus(int statusCode) {
		this.statusCode = statusCode;
	}
	
	public void setError(int status, String message) {
		this.error = true;
		this.statusCode = status;
		this.errorMessage = message;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getCharacterEncoding() {
		return characterEncoding;
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.characterEncoding = characterEncoding;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Headers getHeaders() {
		if (headers == null) {
			headers = new Headers();
		}
		return headers;
	}

	public Cookies getCookies() {
		if (cookies == null) {
			cookies = new Cookies();
		}
		return cookies;
	}
	
	public void setContent(Content content) {
		this.content = content;
	}

	public void serve(HttpServletRequest request, HttpServletResponse response) 
			throws IOException, ServletException {
		
    	if (contentType != null) {
    		response.setContentType(contentType);
    	}
        if (headers != null) {
            headers.addToResponse(response);
        }
        if (cookies != null) {
        	cookies.addToResponse(response);
        }
        if (error) {
			response.sendError(statusCode, errorMessage);
		}
		else {
			if (statusCode > 0) {    
				response.setStatus(statusCode);
			}
			if (content != null) {
				int contentLength = content.getLength(request, response);
				if (contentLength > 0) {
					response.setContentLength(contentLength);
				}
				content.serve(request, response);
			}
		}
    }

	public void delete() {
		if (content != null) {
			content.delete();
		}
	}

}
