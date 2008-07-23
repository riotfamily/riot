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
 *   nd
 *
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.pages.mapping;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Redirects to a given site-chooser-url, if a site was not found.
 * 
 * @author ahe
 */
public class DefaultSiteNotFoundHandler implements SiteNotFoundHandler {

	private String siteChooserUrl;

	public boolean handleSiteNotFound(HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (siteChooserUrl != null) {
			response.sendRedirect(response.encodeRedirectURL(
					request.getContextPath() + siteChooserUrl));
			
			return true;
		}
		return false;
	}

	/**
	 * Sets an URL to which the user will be redirected if no site matches.
	 * Default is <code>null</code>, which means that no redirect is sent and 
	 * the request is handed on to the next plugin in the chain.  
	 * @param  url A context-relative URL
	 */
	public void setSiteChooserUrl(String url) {
		this.siteChooserUrl = url;
	}

}
