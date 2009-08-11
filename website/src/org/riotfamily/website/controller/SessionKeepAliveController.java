package org.riotfamily.website.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

/**
 * Controller that invokes <code>request.getSession(false)</code> to keep an
 * existing HTTP session alive.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class SessionKeepAliveController implements Controller {

	public ModelAndView handleRequest(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {
		
		request.getSession(false);
		return null;
	}

}
