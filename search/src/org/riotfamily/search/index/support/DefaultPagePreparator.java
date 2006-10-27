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
 *   Felix Gnass <fgnass@neteye.de>
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.search.index.support;

import org.riotfamily.search.crawler.support.LinkUtils;
import org.riotfamily.search.index.PagePreparator;
import org.riotfamily.search.parser.Page;

public class DefaultPagePreparator implements PagePreparator {

	private String stripTitlePrefix;
	
	private boolean stripHost;
	
	public void setStripTitlePrefix(String stripTitlePrefix) {
		this.stripTitlePrefix = stripTitlePrefix;
	}
	
	public void setStripHost(boolean stripHost) {
		this.stripHost = stripHost;
	}

	public void preparePage(Page page) {
		page.setUrl(prepareUrl(page.getUrl()));
		String title = page.getTitle();
		if (stripTitlePrefix != null && title != null) {
			if (title.startsWith(stripTitlePrefix)) {
				page.setTitle(title.substring(stripTitlePrefix.length()));
			}
		}
	}
	
	protected String prepareUrl(String url) {
		url = LinkUtils.stripSessionId(url);
		if (stripHost) {
			url = LinkUtils.stripHost(url);
		}
		return url;
	}

}
