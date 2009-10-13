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
package org.riotfamily.common.web.support;

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
