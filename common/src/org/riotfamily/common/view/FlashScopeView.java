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
package org.riotfamily.common.view;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.riotfamily.common.servlet.ServletUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Replacement for Spring's RedirectView that stores the model in the 
 * {@link FlashScope} instead of appending it to the target URL.
 * The {@link FlashScopeInterceptor} must be registered in order to make the
 * data accessible by the next request.
 * 
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 9.0
 */
public class FlashScopeView extends AbstractUrlBasedView {

	private boolean contextRelative = false;

	private boolean http10Compatible = true;

	/**
	 * Constructor for use as a bean.
	 */
	public FlashScopeView() {
	}

	/**
	 * Create a new FlashRedirectView with the given URL.
	 * <p>The given URL will be considered as relative to the web server,
	 * not as relative to the current ServletContext.
	 * @param url the URL to redirect to
	 * @see #FlashRedirectView(String, boolean)
	 */
	public FlashScopeView(String url) {
		super(url);
	}

	/**
	 * Create a new FlashRedirectView with the given URL.
	 * @param url the URL to redirect to
	 * @param contextRelative whether to interpret the given URL as
	 * relative to the current ServletContext
	 */
	public FlashScopeView(String url, boolean contextRelative) {
		super(url);
		this.contextRelative = contextRelative;
	}

	/**
	 * Create a new FlashRedirectView with the given URL.
	 * @param url the URL to redirect to
	 * @param contextRelative whether to interpret the given URL as
	 * relative to the current ServletContext
	 * @param http10Compatible whether to stay compatible with HTTP 1.0 clients
	 */
	public FlashScopeView(String url, boolean contextRelative, boolean http10Compatible) {
		super(url);
		this.contextRelative = contextRelative;
		this.http10Compatible = http10Compatible;
	}

	/**
	 * Set whether to interpret a given URL that starts with a slash ("/")
	 * as relative to the current ServletContext, i.e. as relative to the
	 * web application root.
	 * <p>Default is "false": A URL that starts with a slash will be interpreted
	 * as absolute, i.e. taken as-is. If "true", the context path will be
	 * prepended to the URL in such a case.
	 * @see javax.servlet.http.HttpServletRequest#getContextPath
	 */
	public void setContextRelative(boolean contextRelative) {
		this.contextRelative = contextRelative;
	}

	/**
	 * Set whether to stay compatible with HTTP 1.0 clients.
	 * <p>In the default implementation, this will enforce HTTP status code 302
	 * in any case, i.e. delegate to <code>HttpServletResponse.sendRedirect</code>.
	 * Turning this off will send HTTP status code 303, which is the correct
	 * code for HTTP 1.1 clients, but not understood by HTTP 1.0 clients.
	 * <p>Many HTTP 1.1 clients treat 302 just like 303, not making any
	 * difference. However, some clients depend on 303 when redirecting
	 * after a POST request; turn this flag off in such a scenario.
	 * @see javax.servlet.http.HttpServletResponse#sendRedirect
	 */
	public void setHttp10Compatible(boolean http10Compatible) {
		this.http10Compatible = http10Compatible;
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String url = contextRelative 
				? ServletUtils.resolveUrl(getUrl(), request)
				: getUrl();
				
		FlashScope.store(request, model, ServletUtils.getAbsoluteUrlPrefix(request)+ url);
		sendRedirect(request, response, url, this.http10Compatible);
	}
	
	/**
	 * Send a redirect back to the HTTP client
	 * @param request current HTTP request (allows for reacting to request method)
	 * @param response current HTTP response (for sending response headers)
	 * @param targetUrl the target URL to redirect to
	 * @param http10Compatible whether to stay compatible with HTTP 1.0 clients
	 * @throws IOException if thrown by response methods
	 */
	protected void sendRedirect(
			HttpServletRequest request, HttpServletResponse response, String targetUrl, boolean http10Compatible)
			throws IOException {

		if (http10Compatible) {
			// Always send status code 302.
			response.sendRedirect(response.encodeRedirectURL(targetUrl));
		}
		else {
			// Correct HTTP status code is 303, in particular for POST requests.
			response.setStatus(303);
			response.setHeader("Location", response.encodeRedirectURL(targetUrl));
		}
	}	

}
