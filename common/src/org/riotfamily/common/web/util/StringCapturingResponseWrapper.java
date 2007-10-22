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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.util;

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
