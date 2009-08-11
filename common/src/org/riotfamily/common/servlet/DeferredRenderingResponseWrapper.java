package org.riotfamily.common.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.util.FileCopyUtils;

/**
 * ResponseWrapper that buffers the output and defers the rendering until 
 * {@link #renderResponse()} is invoked.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class DeferredRenderingResponseWrapper extends HttpServletResponseWrapper {	
	
	private ByteArrayOutputStream outputStream;	
	
	private StringWriter writer;
	
	private PrintWriter printWriter;
	
	private boolean redirectSent;
	
	private boolean flush;
	
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
				printWriter = new PrintWriter(writer);
			}
			return printWriter;
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
	
	public void flushBuffer() throws IOException {
		flush = true;
	}
	
	public boolean isRedirectSent() {		
		return redirectSent;
	}
	
	public void renderResponse() throws IOException {
		renderResponse(getResponse());
	}
	
	public void renderResponse(ServletResponse response) throws IOException {
		if (outputStream != null) {			
			FileCopyUtils.copy(outputStream.toByteArray(),
					response.getOutputStream());
		}
		else if (writer != null) {
			response.getWriter().write(writer.toString());
		}
		if (flush) {
			response.flushBuffer();
		}
	}
	
}
