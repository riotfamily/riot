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
 * Portions created by the Initial Developer are Copyright (C) 2007
 * the Initial Developer. All Rights Reserved.
 * 
 * Contributor(s):
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.mapping;

import javax.servlet.http.HttpServletRequest;

import org.riotfamily.common.web.util.PathCompleter;
import org.riotfamily.common.web.util.ServletUtils;
import org.riotfamily.pages.model.Page;
import org.riotfamily.pages.model.Site;

/**
 * @author Felix Gnass [fgnass at neteye dot de]
 * @since 6.5
 */
public class PageUrlBuilder {

	private PathCompleter pathCompleter;

	
	public PageUrlBuilder(PathCompleter pathCompleter) {
		this.pathCompleter = pathCompleter;
	}


	/*
	 * Private helper using the path completer to add the servlet mapping
	 */
	private void completePageUrl(Page page, StringBuffer url) {
		if (!page.isFolder() || pathCompleter.isPrefixMapping()) {
			pathCompleter.addServletMapping(url);
		}
	}

	
	/*
	 * The internals of building the absolute url.
	 */
	protected String getAbsoluteUrl(Page page, HttpServletRequest request, boolean secure) {
		Site site = page.getSite();
		StringBuffer url = getAbsoluteSiteUrl(site, request, secure);
		url.append(page.getPath());
		completePageUrl(page, url);
		return url.toString();
	}

	
	/**
	 * This method returns the raw URL for the given page. This method should only
	 * being used if the page is guaranteed to be on the same site/context. If
	 * unsure have a look at {@link #getUrl(Page, HttpServletRequest)}
	 * 
	 * @param page the url is requested for
	 * @return url for the given page
	 */
	public String getUrl(Page page) {
		StringBuffer url = new StringBuffer(page.getFullPath());
		completePageUrl(page, url);
		return url.toString();
	}

	/**
	 * This method returns an URL for the given page. It works exactly how
	 * {@link #getUrl(Page, HttpServletRequest, boolean)} and determines the
	 * secure flag whether the passed in request is.
	 *  
	 * @param page the url is requested for
	 * @param request the current request
	 * @return an url for the given page
	 */
	public String getUrl(Page page, HttpServletRequest request) {
		return getUrl(page, request, request.isSecure());
	}
	
	/**
	 * This method returns an URL for the given page, but will return a full URL
	 * including the host name if required. This is required if the given request
	 * has been made on a different site where the given page is on and the page's
	 * site has been configured with an host name or if the secure flag differs
	 * from the current request.
	 *  
	 * @param page the url is requested for
	 * @param request the current request
	 * @param secure if true, the {@link ServletUtils.SCHEME_HTTPS} is being used as scheme
	 * @return an url for the given page
	 */
	public String getUrl(Page page, HttpServletRequest request, boolean secure) {
		String siteHost = page.getSite().getHostName();
		if (secure == request.isSecure() && 
				(siteHost == null || request.getServerName().equals(siteHost))) {
			
			return getUrl(page);
		}
		return getAbsoluteUrl(page, request, secure);
	}

	/**
	 * Returns an absolute URL for the given site. It works exactly how
	 * {@link #getAbsoluteSiteUrl(Site, HttpServletRequest, boolean)} and
	 * determines the secure flag whether the passed in request is.
	 * 
	 * @param site the url is requested for
	 * @param request the current request
	 * @return an url for the given site 
	 */
	public StringBuffer getAbsoluteSiteUrl(Site site, HttpServletRequest request) {
		return getAbsoluteSiteUrl(site, request, request.isSecure());
	}
	
	/**
	 * Returns an absolute URL for the given site. It the site does not have a host
	 * name configured the server name of the given request will be used.
	 * 
	 * @param site the url is requested for
	 * @param request the current request
	 * @param secure if true, the {@link ServletUtils.SCHEME_HTTPS} is being used as scheme
	 * @return an url for the given site 
	 */
	public StringBuffer getAbsoluteSiteUrl(Site site, HttpServletRequest request, boolean secure) {
		StringBuffer url = new StringBuffer();
		url.append(secure
				? ServletUtils.SCHEME_HTTPS 
				: ServletUtils.SCHEME_HTTP);
		
        url.append("://");
        if (site.getHostName() != null) {
			url.append(site.getHostName());
		}
		else {
			url.append(request.getServerName());
	        int port = request.getServerPort();
	        if (port <= 0) {
	            port = 80;
	        }
	        // Append the port unless it's the protocol's default 
	        if ((!secure && port != 80) || (secure && port != 443)) {
	        	// If the protocol changes, we don't know the port and need to assume the default 
	        	if (secure == request.isSecure()) {
		            url.append(':');
		            url.append(port);
		        }
	        }
		}
		url.append(request.getContextPath());
		if (site.getPathPrefix() != null) {
			url.append(site.getPathPrefix());
		}
		return url;
	}
	
}
