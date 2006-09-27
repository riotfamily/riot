package org.riotfamily.common.web.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class CapturingResponseWrapper extends HttpServletResponseWrapper {

	private OutputStream targetStream;
	
	private Writer targetWriter;
	
	private ServletOutputStream outputStream;
	
	private PrintWriter writer;
	
	public CapturingResponseWrapper(HttpServletResponse response, 
			OutputStream targetStream) {
		
		super(response);
		this.targetStream = targetStream;
	}
	
	public CapturingResponseWrapper(HttpServletResponse response, 
			Writer targetWriter) {
		
		super(response);
		this.targetWriter = targetWriter;
	}
	
	public ServletOutputStream getOutputStream() throws IOException {
		if (outputStream == null) {
			if (targetStream == null) {
				throw new java.lang.IllegalStateException(
						"The wrapper was set up to capture character data");
			}
			outputStream = new DelegatingServletOutputStream(targetStream);
		}
		return outputStream;
	}
	
	public PrintWriter getWriter() throws IOException {
		if (writer == null) {
			if (targetWriter != null) {
				writer = new PrintWriter(targetWriter);
			}
			else {
				writer = new PrintWriter(targetStream);
			}
		}
		return writer;
	}

}
