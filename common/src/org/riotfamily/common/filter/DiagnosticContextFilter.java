package org.riotfamily.common.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.riotfamily.common.servlet.ServletUtils;
import org.riotfamily.common.util.RiotLog;

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
