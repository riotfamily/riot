package org.riotfamily.cachius.http.content;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionIdDirective implements Directive, ContentFragment {

	private static final String ATTRIBUTE_NAME = SessionIdDirective.class.getName();

	public ContentFragment parse(String expression) {
		if ("sessionid".equals(expression)) {
			return this;
		}
		return null;
	}
	
	private String getSessionTrackingString(HttpServletRequest request) {
		String s = (String) request.getAttribute(ATTRIBUTE_NAME);
		if (s == null) {
			s = "";
			if (!request.isRequestedSessionIdFromCookie()) {
				HttpSession session = request.getSession(false);
				if (session != null) {
					s = ";jsessionid=" + session.getId();
				}
			}
			request.setAttribute(ATTRIBUTE_NAME, s);
		}
		return s;
	}
	
	public int getLength(HttpServletRequest request,
			HttpServletResponse response) {

		return getSessionTrackingString(request).length();
	}
	
	public void serve(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		response.getWriter().print(getSessionTrackingString(request));
	}

}
