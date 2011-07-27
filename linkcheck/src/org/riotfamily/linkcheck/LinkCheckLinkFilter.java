/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.riotfamily.linkcheck;

import org.riotfamily.common.web.support.ServletUtils;
import org.riotfamily.crawler.LinkFilter;

public class LinkCheckLinkFilter implements LinkFilter {

	private static final String[] IGNORED_SCHEMES = { 
		"javascript:", "mailto:", "ftp:" 
	};
	
	public boolean accept(String base, String href) {
		return !ServletUtils.isAbsoluteUrl(href) || !ignoreScheme(href); 
	}
	
	protected boolean ignoreScheme(String href) {
		for (int i = 0; i < IGNORED_SCHEMES.length; i++) {
			if (href.startsWith(IGNORED_SCHEMES[i])) {
				return true;
			}
		}
		return false;
	}
}
