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
import java.net.URISyntaxException;
import java.util.regex.Pattern;

public final class LinkUtils {

	private static Pattern jsessionIdPattern = 
			Pattern.compile(";jsessionid=[^?#]*");
	
	private static Pattern hostPattern = Pattern.compile("^http://[^/]*");
	
	private static Pattern fragmentPattern = Pattern.compile("#.*$");
	
	private LinkUtils() {
	}

	public static String stripSessionId(String url) {
		return jsessionIdPattern.matcher(url).replaceAll("");
	}

	public static String stripHost(String url) {
		return hostPattern.matcher(url).replaceAll("");
	}
	
	public static String stripFragment(String url) {
		return fragmentPattern.matcher(url).replaceAll("");
	}
	
	public static String resolveLink(String baseUrl, String href) {
		try {
			URI baseURI = new URI(baseUrl);
			return baseURI.resolve(href).toString();
		}
		catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalArgumentException e) {
			return href;
		}
	}
	
}
