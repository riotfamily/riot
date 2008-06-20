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
 *   flx
 *
 * ***** END LICENSE BLOCK ***** */
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
