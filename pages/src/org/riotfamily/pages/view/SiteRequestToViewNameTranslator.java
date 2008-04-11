package org.riotfamily.pages.view;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.pages.mapping.PageResolver;
import org.riotfamily.pages.model.Site;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.RequestToViewNameTranslator;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.util.UrlPathHelper;

/**
 * Copy of Spring's {@link DefaultRequestToViewNameTranslator} that strips
 * the site prefix (if present).
 * 
 * @since 7.0
 * @author Felix Gnass [fgnass at neteye dot de]
 */
public class SiteRequestToViewNameTranslator 
		implements RequestToViewNameTranslator {

	private static final String SLASH = "/";


	private String prefix = "";

	private String suffix = "";

	private String separator = SLASH;

	private boolean stripLeadingSlash = true;

	private boolean stripExtension = true;
	
	private boolean stripSitePrefix = true;

	private UrlPathHelper urlPathHelper = new UrlPathHelper();


	/**
	 * Set the prefix to prepend to generated view names.
	 * @param prefix the prefix to prepend to generated view names
	 */
	public void setPrefix(String prefix) {
		this.prefix = (prefix != null ? prefix : "");
	}

	/**
	 * Set the suffix to append to generated view names.
	 * @param suffix the suffix to append to generated view names
	 */
	public void setSuffix(String suffix) {
		this.suffix = (suffix != null ? suffix : "");
	}

	/**
	 * Set the value that will replace '<code>/</code>' as the separator
	 * in the view name. The default behavior simply leaves '<code>/</code>'
	 * as the separator.
	 * @param separator the desired separator value
	 */
	public void setSeparator(String separator) {
		this.separator = separator;
	}

	/**
	 * Set whether or not leading slashes should be stripped from the URI when
	 * generating the view name. Default is "true".
	 * @param stripLeadingSlash <code>true</code> if leading slashes are to be stripped
	 */
	public void setStripLeadingSlash(boolean stripLeadingSlash) {
		this.stripLeadingSlash = stripLeadingSlash;
	}

	/**
	 * Set whether or not file extensions should be stripped from the URI when
	 * generating the view name. Default is "true".
	 * @param stripExtension <code>true</code> if file extensions should be stripped
	 */
	public void setStripExtension(boolean stripExtension) {
		this.stripExtension = stripExtension;
	}

	/**
	 * Set whether or not the site prefix should be stripped. Default is "true".
	 * @param stripSitePrefix <code>true</code> if the site prefix should be stripped
	 */
	public void setStripSitePrefix(boolean stripSitePrefix) {
		this.stripSitePrefix = stripSitePrefix;
	}

	/**
	 * Set if URL lookup should always use the full path within the current servlet
	 * context. Else, the path within the current servlet mapping is used
	 * if applicable (i.e. in the case of a ".../*" servlet mapping in web.xml).
	 * Default is "false".
	 * @param alwaysUseFullPath <code>true</code> if URL lookup should always use the full path
	 * @see org.springframework.web.util.UrlPathHelper#setAlwaysUseFullPath
	 */
	public void setAlwaysUseFullPath(boolean alwaysUseFullPath) {
		this.urlPathHelper.setAlwaysUseFullPath(alwaysUseFullPath);
	}

	/**
	 * Set if the context path and request URI should be URL-decoded.
	 * Both are returned <i>undecoded</i> by the Servlet API,
	 * in contrast to the servlet path.
	 * <p>Uses either the request encoding or the default encoding according
	 * to the Servlet spec (ISO-8859-1).
	 * @see org.springframework.web.util.UrlPathHelper#setUrlDecode
	 */
	public void setUrlDecode(boolean urlDecode) {
		this.urlPathHelper.setUrlDecode(urlDecode);
	}

	/**
	 * Set the {@link org.springframework.web.util.UrlPathHelper} to use for
	 * the resolution of lookup paths.
	 * <p>Use this to override the default UrlPathHelper with a custom subclass,
	 * or to share common UrlPathHelper settings across multiple web components.
	 * @param urlPathHelper the desired helper
	 * @throws IllegalArgumentException if the supplied UrlPathHelper is <code>null</code>
	 */
	public void setUrlPathHelper(UrlPathHelper urlPathHelper) {
		Assert.notNull(urlPathHelper, "UrlPathHelper must not be null");
		this.urlPathHelper = urlPathHelper;
	}


	/**
	 * Translates the request URI of the incoming {@link HttpServletRequest}
	 * into the view name based on the configured parameters.
	 * @see org.springframework.web.util.UrlPathHelper#getLookupPathForRequest
	 * @see #transformPath
	 */
	public String getViewName(HttpServletRequest request) {
		String lookupPath = this.urlPathHelper.getLookupPathForRequest(request);
		return (this.prefix + transformPath(lookupPath, request) + this.suffix);
	}

	/**
	 * Transform the request URI (in the context of the webapp) stripping
	 * slashes and extensions, and replacing the separator as required.
	 */
	protected String transformPath(String lookupPath, HttpServletRequest request) {
		String path = lookupPath;
		if (this.stripSitePrefix) {
			Site site = PageResolver.getResolvedSite(request);
			if (site != null && site.prefixMatches(path)) {
				path = site.stripPrefix(path);
			}
		}
		if (this.stripLeadingSlash && path.startsWith(SLASH)) {
			path = path.substring(1);
		}
		if (this.stripExtension) {
			path = StringUtils.stripFilenameExtension(path);
		}
		if (!SLASH.equals(this.separator)) {
			path = StringUtils.replace(path, SLASH, this.separator);
		}
		return path;
	}
}
