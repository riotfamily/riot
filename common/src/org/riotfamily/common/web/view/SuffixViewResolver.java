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
package org.riotfamily.common.web.view;

import java.util.Locale;
import java.util.Map;

import org.riotfamily.common.util.RiotLog;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * View resolver that supports view names with a suffix. For example
 * <tt>someView.jsp</tt> would be delegated to the ViewResolver
 * registered for the <tt>jsp</tt> suffix.
 */
public class SuffixViewResolver extends AbstractCachingViewResolver 
		implements Ordered {

	/**
	 * Prefix for special view names that specify a redirect URL (usually
	 * to a controller after a form has been submitted and processed).
	 * Such view names will not be resolved in the configured default
	 * way but rather be treated as special shortcut.
	 */
	public static final String REDIRECT_URL_PREFIX = "redirect:";

	/**
	 * Prefix for special view names that specify a forward URL (usually
	 * to a controller after a form has been submitted and processed).
	 * Such view names will not be resolved in the configured default
	 * way but rather be treated as special shortcut.
	 */
	public static final String FORWARD_URL_PREFIX = "forward:";
	
	private RiotLog log = RiotLog.get(SuffixViewResolver.class);
	
	private Map<String, ViewResolver> resolvers;

	private String defaultSuffix;

	private ViewResolver defaultResolver;

	private boolean redirectContextRelative = true;

	private boolean redirectHttp10Compatible = true;
	
	private int order = Ordered.LOWEST_PRECEDENCE;
	
	/**
	 * @param resolvers ViewResolvers keyed by suffix (without dots).
	 */
	public void setResolvers(Map<String, ViewResolver> resolvers) {
		this.resolvers = resolvers;
	}
	
	/**
	 * Sets a default suffix that will be appended to view names that don't
	 * contain a dot. If set to <code>null</code> (default), view names without
	 * an extension won't be resolved which allows resolver chaining.
	 */
	public void setDefaultSuffix(String defaultSuffix) {
		this.defaultSuffix = defaultSuffix;
	}

	/**
	 * Set whether to interpret a given redirect URL that starts with a
	 * slash ("/") as relative to the current ServletContext, i.e. as
	 * relative to the web application root.
	 * <p>Default is "true": A redirect URL that starts with a slash will be
	 * interpreted as relative to the web application root, i.e. the context
	 * path will be prepended to the URL.
	 * <p><b>Redirect URLs can be specified via the "redirect:" prefix.</b>
	 * E.g.: "redirect:myAction.do"
	 * @see RedirectView#setContextRelative
	 * @see #REDIRECT_URL_PREFIX
	 */
	public void setRedirectContextRelative(boolean redirectContextRelative) {
		this.redirectContextRelative = redirectContextRelative;
	}

	/**
	 * Set whether redirects should stay compatible with HTTP 1.0 clients.
	 * <p>In the default implementation, this will enforce HTTP status code 302
	 * in any case, i.e. delegate to <code>HttpServletResponse.sendRedirect</code>.
	 * Turning this off will send HTTP status code 303, which is the correct
	 * code for HTTP 1.1 clients, but not understood by HTTP 1.0 clients.
	 * <p>Many HTTP 1.1 clients treat 302 just like 303, not making any
	 * difference. However, some clients depend on 303 when redirecting
	 * after a POST request; turn this flag off in such a scenario.
	 * <p><b>Redirect URLs can be specified via the "redirect:" prefix.</b>
	 * E.g.: "redirect:myAction.do"
	 * @see RedirectView#setHttp10Compatible
	 * @see #REDIRECT_URL_PREFIX
	 */
	public void setRedirectHttp10Compatible(boolean redirectHttp10Compatible) {
		this.redirectHttp10Compatible = redirectHttp10Compatible;
	}
	
	/**
	 * Set the order in which this {@link org.springframework.web.servlet.ViewResolver}
	 * is evaluated. Default is {@link Ordered#LOWEST_PRECEDENCE}.
	 */
	public void setOrder(int order) {
		this.order = order;
	}

	/**
	 * Return the order in which this {@link org.springframework.web.servlet.ViewResolver}
	 * is evaluated.
	 */
	public int getOrder() {
		return this.order;
	}
	
	protected View loadView(String viewName, Locale locale) throws Exception {
		
		// Check for special "redirect:" prefix.
		if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
			String redirectUrl = viewName.substring(REDIRECT_URL_PREFIX.length());
			return new RedirectView(redirectUrl, redirectContextRelative, 
					redirectHttp10Compatible);
		}

		// Check for special "forward:" prefix.
		if (viewName.startsWith(FORWARD_URL_PREFIX)) {
			String forwardUrl = viewName.substring(FORWARD_URL_PREFIX.length());
			return new InternalResourceView(forwardUrl);
		}
				
		String suffix = null;
		ViewResolver resolver = null;
		int i = viewName.lastIndexOf('.');
		if (i > 0) {
			suffix = viewName.substring(i + 1);
			resolver = resolvers.get(suffix);
			if (log.isDebugEnabled()) {
				if (resolver != null) {
					log.debug("Using " + resolver + " for suffix [" 
							+ suffix + ']');
				}
			}
		}
		
		if (resolver == null) {
			if (suffix != null) {
				throw new IllegalArgumentException("No resolver defined for " +
						"suffix [" + suffix + ']');
			}
			if (defaultSuffix != null) {
				viewName += "." + defaultSuffix;
				resolver = defaultResolver;
			}
			else {
				return null;
			}
		}
		return resolver.resolveViewName(viewName, locale);
	}

}
