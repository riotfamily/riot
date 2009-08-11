package org.riotfamily.website.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that sends a HTTP error response with a configurable message 
 * and status code.
 *  
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class HttpErrorController implements Controller {

	private int statusCode;
	
	private String message;

	public HttpErrorController(int code) {
		this.statusCode = code;
	}

	public HttpErrorController(int code, String message) {
		this.statusCode = code;
		this.message = message;
	}

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		response.sendError(statusCode, message);
		return null;
	}

}
