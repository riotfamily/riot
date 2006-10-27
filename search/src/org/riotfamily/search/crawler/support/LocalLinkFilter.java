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

import java.net.URI;
import java.util.regex.Pattern;

import org.riotfamily.search.crawler.LinkFilter;

public class LocalLinkFilter implements LinkFilter {

	private Pattern pathPattern;
	
	public LocalLinkFilter() {
	}

	public LocalLinkFilter(String pathPattern) {
		if (pathPattern != null) {
			this.pathPattern = Pattern.compile(pathPattern);
		}
	}
	
	public boolean accept(String baseUrl, String link) {
		try {
			URI baseURI = new URI(baseUrl);
			URI linkURI = baseURI.resolve(link);
			if (isValidHost(baseURI, linkURI)) {
				if (pathPattern != null) {
					return pathPattern.matcher(linkURI.getPath()).matches();
				}
				else {
					return true;
				}
			}
		}
		catch (Exception e) {
		}
		return false;
	}
	
	protected boolean isValidHost(URI baseURI, URI linkURI) {
		return baseURI.getHost().equals(linkURI.getHost());
	}

}
