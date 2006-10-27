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
package org.riotfamily.search.crawler.support;

import java.util.HashSet;
import java.util.Stack;

import org.riotfamily.search.crawler.CrawlerModel;

public class DefaultCrawlerModel implements CrawlerModel {

	private HashSet knownUrls = new HashSet();

	private Stack urls = new Stack();
	
	public boolean hasNextUrl() {
		return !urls.isEmpty();
	}

	public String getNextUrl() {
		return (String) urls.pop();
	}

	public void addUrl(String url) {
		String lookupUrl = normalizeUrl(url);
		if (!knownUrls.contains(lookupUrl)) {
			knownUrls.add(lookupUrl);
			urls.push(url);
		}
	}

	protected String normalizeUrl(String url) {
		return LinkUtils.stripSessionId(url);
	}

}
