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
 *   Felix Gnass [fgnass at neteye dot de]
 * 
 * ***** END LICENSE BLOCK ***** */
package org.riotfamily.core.resource;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

public abstract class AbstractPathMatchingResourceFilter 
		implements ResourceFilter {

	private String[] matches;
	
	private PathMatcher pathMatcher = new AntPathMatcher();
	
	public void setMatch(String match) {
		this.matches = new String[] { match };
	}
	
	public void setMatches(String[] matches) {
		this.matches = matches;
	}

	public void setPathMatcher(PathMatcher pathMatcher) {
		this.pathMatcher = pathMatcher;
	}
	
	public boolean matches(String path) {
		for (int i = 0; i < matches.length; i++) {
			if (pathMatcher.match(matches[i], path)) {
				return true;
			}
		}
		return false;
	}

}
