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
package org.riotfamily.common.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.common.util.RiotLog;
import org.riotfamily.common.web.util.ServletUtils;

public class DiagnosticContextFilter extends HttpFilterBean {

	private static final String URL = "URL";
	
	private static final String IP = "IP";

	private static final String REFERER = "Referer";
	
	private static final String USER_AGENT = "User-Agent";
	
	private static final String SESSION_ID = "sessionId";

	@Override
	protected void filter(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		RiotLog.clear();
		RiotLog.setClearMdcDeferred(true);

		String ip = request.getRemoteAddr();
		String url = ServletUtils.getRequestUrlWithQueryString(request);
		String referer = ServletUtils.getReferer(request);
		String userAgent = ServletUtils.getUserAgent(request);
		HttpSession session = request.getSession(false);

		RiotLog.put(IP, ip);
		RiotLog.put(URL, url);
		if (referer != null) {
			RiotLog.put(REFERER, referer);
		}
		if (userAgent != null) {
			RiotLog.put(USER_AGENT, userAgent);
		}
		if (session != null) {
			RiotLog.put(SESSION_ID, session.getId());
		}

		RiotLog.push(" [" + ip + " => " + url + "]");

		chain.doFilter(request, response);
	}

	
}
