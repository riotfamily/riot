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
package org.riotfamily.common.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.common.web.support.ServletUtils;
import org.slf4j.MDC;

public class DiagnosticContextFilter extends HttpFilterBean {

	private static final String MDC_IS_CLEARED_ON_EACH_REQUEST = 
		"_MDC is cleared on each request";

	private static final String URL = "URL";
	
	private static final String IP = "IP";

	private static final String REFERER = "Referer";
	
	private static final String USER_AGENT = "User-Agent";
	
	private static final String SESSION_ID = "sessionId";

	@Override
	protected void filter(HttpServletRequest request,
			HttpServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		MDC.clear();
		MDC.put(MDC_IS_CLEARED_ON_EACH_REQUEST, "true");
		
		String ip = request.getRemoteAddr();
		String url = ServletUtils.getOriginatingRequestUrlWithQueryString(request);
		String referer = ServletUtils.getReferer(request);
		String userAgent = ServletUtils.getUserAgent(request);
		HttpSession session = request.getSession(false);

		MDC.put(IP, ip);
		MDC.put(URL, url);
		if (referer != null) {
			MDC.put(REFERER, referer);
		}
		if (userAgent != null) {
			MDC.put(USER_AGENT, userAgent);
		}
		if (session != null) {
			MDC.put(SESSION_ID, session.getId());
		}
		chain.doFilter(request, response);
	}

	public static boolean isPresent() {
		return MDC.get(MDC_IS_CLEARED_ON_EACH_REQUEST) != null;
	}

	
}
