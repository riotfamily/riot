/* ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1
 * The contents of this file are subject to the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 * 
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 * 
 * The Original Code is Riot.
 * 
 * The Initial Developer of the Original Code is
 * Neteye GmbH.
 * Portions created by the Initial Developer are Copyright (C) 2006
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
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
 * 
 * @see IncludeFirstInterceptor
 * @author Felix Gnass [fgnass at neteye dot de]
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
	
	public void renderResponse(HttpServletResponse response) throws IOException {
		if (outputStream != null) {			
			FileCopyUtils.copy(outputStream.toByteArray(),
					response.getOutputStream());
		}
		else if (writer != null) {
			response.getWriter().write(writer.toString());
		}
	}
	
	
}
