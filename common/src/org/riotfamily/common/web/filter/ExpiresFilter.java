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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.common.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.FormatUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Servlet filter that sets an <code>Expires</code> header for each request.
 */
public class ExpiresFilter extends GenericFilterBean {

	private static final String HEADER_EXPIRES = "Expires";

	private long expiresAfter;
	
	public void setExpiresAfter(String expires) {
		this.expiresAfter = FormatUtils.parseMillis(expires);
	}
	
	public void doFilter(ServletRequest request, ServletResponse response, 
			FilterChain filterChain) throws IOException, ServletException {
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setDateHeader(HEADER_EXPIRES, 
				System.currentTimeMillis() + expiresAfter);
		
		filterChain.doFilter(request, response);
	}

}
