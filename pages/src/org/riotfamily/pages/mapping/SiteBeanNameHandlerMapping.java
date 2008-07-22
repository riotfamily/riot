package org.riotfamily.pages.mapping;

import java.util.Collections;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.mapping.AdvancedBeanNameHandlerMapping;
import org.riotfamily.common.web.mapping.UrlResolverContext;
import org.riotfamily.pages.model.Site;

/**
 * AdvancedBeanNameHandlerMapping that can fill in the path prefix of the 
 * current Site.
 * <p>
 * Lets assume you have a controller with the following mapping:
 * <pre>
 * &lt;bean id="foo" name="${sitePrefix*}/foo.html" ... &gt;
 * </pre>
 * Then writing <code>${common.urlForHandler('foo')}</code> in your .ftl will
 * result in a valid URL, prefixed with the current Site's pathPrefix.
 * <p>
 * Note: The mapping currently doesn't perform any sanity check upon lookup,
 * i.e. the pattern above will also match "/bar/baz/foo.html", even though
 * "/bar/baz" is no valid Site prefix.
 * </p>
 * @author flx
 */
public class SiteBeanNameHandlerMapping extends AdvancedBeanNameHandlerMapping {

	private PageResolver pageResolver;
	
	public SiteBeanNameHandlerMapping(PageResolver pageResolver) {
		this.pageResolver = pageResolver;
	}
	
	public Object getHandlerInternal(HttpServletRequest request)
			throws Exception {

		Object handler = super.getHandlerInternal(request);
		if (handler != null) {
			pageResolver.getSite(request);
		}
		return handler;
	}

	protected Map<String, ?> getDefaults(UrlResolverContext context) {
		Site site = PageResolver.getResolvedSite(context);
		String sitePrefix = null;
		if (site != null) {
			sitePrefix = site.getPathPrefix();
		}
		if (sitePrefix == null) {
			sitePrefix = "";
		}
		return Collections.singletonMap("sitePrefix", sitePrefix);
	}
}
