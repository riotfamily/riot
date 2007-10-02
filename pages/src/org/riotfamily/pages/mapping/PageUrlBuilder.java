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

import org.riotfamily.common.web.util.PathCompleter;
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
	
	public String getUrl(Page page) {
		StringBuffer url = new StringBuffer();
		Site site = page.getSite();
		if (site.getPathPrefix() != null) {
			url.append(site.getPathPrefix());
		}
		url.append(page.getPath());
		if (!page.isFolder() || pathCompleter.isPrefixMapping()) {
			pathCompleter.addServletMapping(url);
		}
		return url.toString();
	}
}
