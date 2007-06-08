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
package org.riotfamily.common.web.filter;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.util.FormatUtils;
import org.riotfamily.common.web.util.ServletUtils;
import org.springframework.web.filter.GenericFilterBean;

/**
 * Servlet filter that sets an Expires header for request URLs that contain
 * a timestamp. URLs are considered as 'stamped' if they match the configured
 * pattern.
 * <p>
 * The default pattern is <code>(/\\d{14}/)|(\\?[0-9]+$)</code>, this
 * matches URLs created by the DefaultFileStore and URLs printed via the
 * <code>common.resource()</code> FreeMarker function.
 * </p>
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.4
 */
public class CacheStampedResourcesFilter extends GenericFilterBean {

	public static final String DEFAULT_EXPIRATION = "1M";

	public static final Pattern DEFAULT_PATTERN =
			Pattern.compile("(/\\d{14}/)|(\\?[0-9]+$)");

	private static final String EXPIRES_HEADER = "Expires";

	private Pattern stampPattern = DEFAULT_PATTERN;

	private String expiresAfter = DEFAULT_EXPIRATION;

	private long expires;

	public void setExpiresAfter(String expiresAfter) {
		this.expiresAfter = expiresAfter;
	}

	public void setStampPattern(Pattern stampPattern) {
		this.stampPattern = stampPattern;
	}

	protected void initFilterBean() throws ServletException {
		expires = System.currentTimeMillis()
				+ FormatUtils.parseMillis(expiresAfter);
	}

	public final void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws IOException, ServletException {

		doFilterInternal((HttpServletRequest) request,
				(HttpServletResponse) response, filterChain);
	}

	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (isStamped(request)) {
			response.setDateHeader(EXPIRES_HEADER, expires);
		}
		filterChain.doFilter(request, response);
	}

	protected boolean isStamped(HttpServletRequest request) {
		String url = ServletUtils.getRequestUrlWithQueryString(request);
		return stampPattern.matcher(url).matches();
	}
}
