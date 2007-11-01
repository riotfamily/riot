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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Writer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * ResponseWrapper that captures the output and redirects it to another
 * OutputStream or Writer.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 */
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
	
	public void flush() throws IOException {
		if (outputStream != null) {
			outputStream.flush();
		}
		if (writer != null) {
			writer.flush();
		}
	}

}
