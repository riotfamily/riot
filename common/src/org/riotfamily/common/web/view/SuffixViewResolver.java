package org.riotfamily.common.web.view;

import java.util.Locale;
import java.util.Map;

import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.view.AbstractCachingViewResolver;
import org.springframework.web.servlet.view.InternalResourceView;

/**
 * View resolver that supports view names with a suffix. For example
 * <tt>someView.jsp</tt> would be delegated to the ViewResolver
 * registered for the <tt>jsp</tt> suffix.
 */
public class SuffixViewResolver extends AbstractCachingViewResolver {

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
	
	/**
	 * TODO
	 */
	public static final String CONTROLLER_PREFIX = "controller:";
	
	private Map resolvers;

	private String defaultSuffix;

	private ViewResolver defaultResolver;

	private boolean redirectContextRelative = true;

	private boolean redirectHttp10Compatible = true;
	
	/**
	 * @param resolvers ViewResolvers keyed by suffix (without dots).
	 */
	public void setResolvers(Map resolvers) {
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
		
		// Check for special "controller:" prefix.
		if (viewName.startsWith(CONTROLLER_PREFIX)) {
			String controllerName = viewName.substring(CONTROLLER_PREFIX.length());
			Controller controller = (Controller) getApplicationContext()
					.getBean(controllerName, Controller.class);
			
			return new ControllerView(controller, this);
		}
		
		String suffix = null;
		ViewResolver resolver = null;
		int i = viewName.lastIndexOf('.');
		if (i > 0) {
			suffix = viewName.substring(i + 1);
			resolver = (ViewResolver) resolvers.get(suffix);
			if (logger.isDebugEnabled()) {
				if (resolver != null) {
					logger.debug("Using " + resolver + " for suffix [" 
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
