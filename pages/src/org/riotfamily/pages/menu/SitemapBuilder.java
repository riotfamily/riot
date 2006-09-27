package org.riotfamily.pages.menu;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

public interface SitemapBuilder {
	
	/**
	 * Returns a timestamp indicating when the sitemap structure for the given
	 * request has changed for the last time. This information is used for
	 * caching purposes.
	 */
	public long getLastModified(HttpServletRequest request);

	/**
	 * Returns a list of all toplevel {@link MenuItem MenuItem}s. The items
	 * must be initialized with child items so that the returned structure 
	 * represents the complete sitemap tree.
	 */
	public List buildSitemap(HttpServletRequest request);

}
