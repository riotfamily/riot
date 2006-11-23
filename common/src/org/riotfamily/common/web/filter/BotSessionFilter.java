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
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.springframework.web.filter.OncePerRequestFilter;

/**
 * Filter that prevents URLs from beeing encoded if the request is originated 
 * by search engine robot/crawler.
 * 
 * @since 6.4
 * @author Felix Gnass <fgnass@neteye.de>
 */
public class BotSessionFilter extends OncePerRequestFilter {

	public static final String DEFAULT_BOT_PATTERN = "bot|crawler|spider";
	
	private static final String USER_AGENT_HEADER = "User-Agent";
	
	private Pattern pattern = Pattern.compile(DEFAULT_BOT_PATTERN);
	
	/**
	 * Sets the regular expression that is used to check whether a User-Agent
	 * belongs to a robot/spider/crawler. The pattern does not need to match
	 * the whole User-Agent header, it is checked whether the patter is 
	 * <i>included</i> within the string. Note that the pattern is 
	 * <b>case sensitive</b> and the header is <b>converted to lower case</b>
	 * before the pattern is checked. The default is 
	 * <code>bot|crawler|spider</code>.  
	 */
	public void setPattern(String pattern) {
		this.pattern = Pattern.compile(pattern);
	}
	
	/**
	 * Skips the filtering if a session id was requested using a cookie or
	 * the User-Agent header does not look like the one of a robot. 
	 */
	protected boolean shouldNotFilter(HttpServletRequest request) {
		return request.isRequestedSessionIdFromCookie() || !isBot(request);
	}
	
	/**
	 * Returns <code>true</code> if the botPattern is found in the 
	 * User-Agent header. Note that the header is converted to lower case 
	 * before the regular expression is checked.
	 */
	protected boolean isBot(HttpServletRequest request) {
		String agent = request.getHeader(USER_AGENT_HEADER);
		if (agent != null) {
			return pattern.matcher(agent.toLowerCase()).find();
		}
		return false;
	}
	
	/**
	 * Passes the request on to the filter chain using a response wrapper that
	 * prevents URLs from beeing encoded. 
	 */
	protected final void doFilterInternal(HttpServletRequest request, 
			HttpServletResponse response, FilterChain filterChain) 
			throws ServletException, IOException {
		
		filterChain.doFilter(request, new NoRewriteResponse(response));
	}
	
	/**
	 * Response wrapper that prevents URLs from beeing encoded.
	 */
	private static class NoRewriteResponse extends HttpServletResponseWrapper {
		
		public NoRewriteResponse(HttpServletResponse response) {
			super(response);
		}
		
		public String encodeRedirectUrl(String url) {
			return url;
		}
		
		public String encodeRedirectURL(String url) {
			return url;
		}
		
		public String encodeUrl(String url) {
			return url;
		}
		
		public String encodeURL(String url) {
			return url;
		}
	}

}
