package org.riotfamily.cachius.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.riotfamily.cachius.support.SessionIdEncoder;

public class SessionIdInsertingResponseWrapper extends HttpServletResponseWrapper {

	private SessionIdEncoder sessionIdEncoder;
	
	public SessionIdInsertingResponseWrapper(HttpServletResponse response, 
			SessionIdEncoder sessionIdEncoder) {
		
		super(response);
		this.sessionIdEncoder = sessionIdEncoder;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(sessionIdEncoder.createIdInsertingWriter(super.getWriter()));
	}
	
}
