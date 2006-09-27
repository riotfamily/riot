package org.riotfamily.common.web.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.util.FileCopyUtils;

/**
 * ResponseWrapper that buffers the output and defers the rendering until 
 * {@link #renderResponse()} is invoked.
 */
public class DeferredRenderingResponseWrapper extends HttpServletResponseWrapper {	
	
	private ByteArrayOutputStream outputStream;	
	
	private StringWriter writer;
	
	private boolean redirectSent = false;
	
	public DeferredRenderingResponseWrapper(HttpServletResponse response) {
		super(response);		
	}	
	
	public void sendError(int sc) throws IOException {		
		redirectSent = true;
		super.sendError(sc);
	}
	
	public void sendError(int sc, String msg) throws IOException {		
		redirectSent = true;
		super.sendError(sc, msg);
	}
	
	public void sendRedirect(String location) throws IOException {		
		redirectSent = true;
		super.sendRedirect(location);
	}
		
	public PrintWriter getWriter() throws IOException {
		if (outputStream == null) {
			if (writer == null) {
				writer = new StringWriter();
			}
			return new PrintWriter(writer);
		}
		else {
			throw new IllegalStateException(
				"getOutputStream() has been called already");
		}
	}
	
	public ServletOutputStream getOutputStream() throws IOException {
		if (writer == null) {
			if (outputStream == null) {
				outputStream = new ByteArrayOutputStream();
			}
			return new DelegatingServletOutputStream(outputStream);
		}
		else {
			throw new IllegalStateException(
					"getWriter() has been called already");
		}
	}
	
	public boolean isRedirectSent() {		
		return redirectSent;
	}
	
	public void renderResponse() throws IOException {
		if (outputStream != null) {			
			FileCopyUtils.copy(outputStream.toByteArray(),
						getResponse().getOutputStream());
		}
		else if (writer != null) {
			getResponse().getWriter().write(writer.toString());
		}
	}
	
	
}
