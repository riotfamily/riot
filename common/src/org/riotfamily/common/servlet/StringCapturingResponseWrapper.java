package org.riotfamily.common.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class StringCapturingResponseWrapper extends HttpServletResponseWrapper {

	private StringWriter stringWriter;
	
	private PrintWriter printWriter;
	
	public StringCapturingResponseWrapper(HttpServletResponse response) {
		super(response);
		stringWriter = new StringWriter();
		printWriter = new PrintWriter(stringWriter);
	}
	
	public PrintWriter getWriter() throws IOException {
		return printWriter;
	}
	
	public String getCapturedData() {
		printWriter.flush();
		return stringWriter.toString();
	}
	
	public ServletOutputStream getOutputStream() throws IOException {
		throw new IOException("This ResponseWrapper must only be used " +
				"for character data.");
	}
	
}
